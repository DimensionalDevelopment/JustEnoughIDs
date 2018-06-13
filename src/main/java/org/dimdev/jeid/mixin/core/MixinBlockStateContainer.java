package org.dimdev.jeid.mixin.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BitArray;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.NibbleArray;
import org.dimdev.jeid.INewBlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(BlockStateContainer.class)
public abstract class MixinBlockStateContainer implements INewBlockStateContainer {
    @Shadow protected abstract IBlockState get(int index);
    @Shadow @SuppressWarnings("unused") protected BitArray storage;
    @Shadow @SuppressWarnings("unused") protected IBlockStatePalette palette;
    @Shadow protected abstract void set(int index, IBlockState state);
    @Shadow @SuppressWarnings("unused") protected abstract void setBits(int bitsIn);

    private int[] temporaryPalette; // index -> state id

    @Override
    public int[] getTemporaryPalette() {
        return temporaryPalette;
    }

    @Override
    public void setTemporaryPalette(int[] temporaryPalette) {
        this.temporaryPalette = temporaryPalette;
    }

    /**
     * @reason If this BlockStateContainer should be saved in JustEnoughIDs format,
     * store palette IDs rather than block IDs in the container's "Blocks" and
     * "Data" arrays.
     */
    @SuppressWarnings("deprecation")
    @Inject(method = "getDataForNBT", at = @At("HEAD"), cancellable = true)
    private void newGetDataForNBT(byte[] blockIds, NibbleArray data, CallbackInfoReturnable<NibbleArray> cir) {
        HashMap<IBlockState, Integer> stateIDMap = new HashMap<>();
        int nextID = 0;
        for (int index = 0; index < 4096; ++index) {
            IBlockState state = get(index);
            Integer paletteID = stateIDMap.get(state);
            if (paletteID == null) {
                paletteID = nextID++;
                stateIDMap.put(state, paletteID);
            }

            int x = index & 15;
            int y = index >> 8 & 15;
            int z = index >> 4 & 15;

            blockIds[index] = (byte) (paletteID >> 4 & 255);
            data.set(x, y, z, paletteID & 15);
        }

        temporaryPalette = new int[nextID];
        for (Map.Entry<IBlockState, Integer> entry : stateIDMap.entrySet()) {
            temporaryPalette[entry.getValue()] = Block.BLOCK_STATE_IDS.get(entry.getKey());
        }

        cir.setReturnValue(null);
        cir.cancel();
    }

    /**
     * @reason If this BlockStateContainer is saved in JustEnoughIDs format, treat
     * the "Blocks" and "Data" arrays as palette IDs.
     */
    @SuppressWarnings("deprecation")
    @Inject(method = "setDataFromNBT", at = @At("HEAD"), cancellable = true)
    private void newSetDataFromNBT(byte[] blockIds, NibbleArray data, NibbleArray blockIdExtension, CallbackInfo ci) {
        if (temporaryPalette == null) return; // Read containers in in pallette format only if the container has a palette (has a palette)

        for (int index = 0; index < 4096; ++index) {
            int x = index & 15;
            int y = index >> 8 & 15;
            int z = index >> 4 & 15;
            int paletteID = (blockIds[index] & 255) << 4 | data.get(x, y, z);

            set(index, Block.BLOCK_STATE_IDS.getByValue(temporaryPalette[paletteID]));
        }

        temporaryPalette = null;
        ci.cancel();
    }
}
