package org.dimdev.jeid.mixin.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkPrimer.class)
@SuppressWarnings("deprecation")
public abstract class MixinChunkPrimer {
    @Shadow private static int getBlockIndex(int x, int y, int z) { return 0; }
    @Shadow @Final private static IBlockState DEFAULT_STATE;
    private int[] intData = new int[65536];

    @Overwrite
    public IBlockState getBlockState(int x, int y, int z) {
        IBlockState state = Block.BLOCK_STATE_IDS.getByValue(intData[getBlockIndex(x, y, z)]);
        return state == null ? DEFAULT_STATE : state;
    }

    @Overwrite
    public void setBlockState(int x, int y, int z, IBlockState state) {
        intData[getBlockIndex(x, y, z)] = Block.BLOCK_STATE_IDS.get(state);
    }

    @Overwrite
    public int findGroundBlockIdx(int x, int z) {
        int xz = (x << 12 | z << 8) + 256 - 1;
        for (int y = 255; y >= 0; --y) {
            IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(intData[xz + y]);
            if (iblockstate != null && iblockstate != DEFAULT_STATE) return y;
        }

        return 0;
    }
}
