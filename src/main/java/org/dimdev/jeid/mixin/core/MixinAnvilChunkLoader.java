package org.dimdev.jeid.mixin.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.INewBlockStateContainer;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader {
    /** @reason Read palette from NBT for JustEnoughIDs BlockStateContainers. */
    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;<init>(IZ)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readPaletteNBT(World world, NBTTagCompound nbt, CallbackInfoReturnable<Chunk> cir,
                                int ignored0, int ignored1, Chunk ignored2, NBTTagList ignored3, int ingnored4, ExtendedBlockStorage[] ignored5, boolean ignored6, int ignored7,
                                NBTTagCompound storageNBT, int y, ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = storageNBT.hasKey("Palette", 11) ? storageNBT.getIntArray("Palette") : null;
        ((INewBlockStateContainer) extendedBlockStorage.getData()).setTemporaryPalette(palette);
    }

    /** @reason Write palette to NBT for JustEnoughIDs BlockStateContainers. */
    @Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/chunk/BlockStateContainer;getDataForNBT([BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void writePaletteNBT(Chunk chunk, World worldIn, NBTTagCompound nbt, CallbackInfo ci,
                                 ExtendedBlockStorage[] ignored0, NBTTagList ignored1, boolean ignored2, ExtendedBlockStorage[] ignored3, int ignored4, int ignored5,
                                 ExtendedBlockStorage extendedBlockStorage, NBTTagCompound storageNBT, byte[] blocks, NibbleArray data, NibbleArray add) {
        int[] palette = ((INewBlockStateContainer) extendedBlockStorage.getData()).getTemporaryPalette();
        if (palette != null) storageNBT.setIntArray("Palette", palette);
    }

    /** @reason Read int biome array from NBT if it's there. */
    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;hasKey(Ljava/lang/String;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readBiomeArray(World world, NBTTagCompound nbt, CallbackInfoReturnable<Chunk> cir,
                                int ignored0, int ignored1,
                                Chunk chunk,
                                NBTTagList ignored2, int ignored3, ExtendedBlockStorage[] ignored4, boolean ignored5) {
        INewChunk newChunk = (INewChunk) chunk;
        if (nbt.hasKey("Biomes", 11)) {
            newChunk.setIntBiomeArray(nbt.getIntArray("Biomes"));
        } else {
            // Convert old chunks
            int[] intBiomeArray = new int[256];
            int index = 0;
            for (byte b : nbt.getByteArray("Biomes")) {
                intBiomeArray[index++] = b & 0xFF;
            }
            newChunk.setIntBiomeArray(intBiomeArray);
        }
    }

    /** @reason Disable default biome array save logic. */
    @Redirect(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", ordinal = 6))
    private void defaultWriteBiomeArray(NBTTagCompound nbt, String key, byte[] value) {
        if (!key.equals("Biomes")) throw new AssertionError("Ordinal 6 of setByteArray isn't \"Biomes\"");
    }

    /** @reason Disable default biome array save logic. */
    @Redirect(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", ordinal = 0))
    private byte[] defaultWriteBiomeArray(Chunk chunk) {
        return new byte[0];
    }

    /** @reason Save the correct biome array type */
    @Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", ordinal = 6))
    private void writeBiomeArray(Chunk chunk, World worldIn, NBTTagCompound nbt, CallbackInfo ci) {
        INewChunk newChunk = (INewChunk) chunk;
        nbt.setIntArray("Biomes", newChunk.getIntBiomeArray());
    }
}
