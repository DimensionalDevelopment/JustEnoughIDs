package org.dimdev.jeid.mixin.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;

@Mixin(Chunk.class)
public class MixinChunk implements INewChunk {
    private static final byte errorBiomeID = (byte) Biome.REGISTRY.getIDForObject(JEID.errorBiome);
    private final int[] intBiomeArray = generateIntBiomeArray();

    private static int[] generateIntBiomeArray() {
        int[] arr = new int[256];
        Arrays.fill(arr, -1);
        return arr;
    }

    @Override
    public int[] getIntBiomeArray() {
        return intBiomeArray;
    }

    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        System.arraycopy(intBiomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
    }

    @Overwrite
    public byte[] getBiomeArray() {
        Utils.LOGGER.error("A mod is accessing the byte biome array, report to JEID!", new Throwable("Chunk#getBiomeArray"));
        byte[] arr = new byte[256];
        Arrays.fill(arr, errorBiomeID);
        return arr;
    }

    @Redirect(method = "getBiome", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B", args = "array=get"))
    private int getIntBiomeIdFromArray(byte[] array, int index) {
        return this.intBiomeArray[index];
    }

    @Inject(method = "getBiome", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B", args = "array=set"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setIntBiomeIdInArray(BlockPos pos, BiomeProvider provider, CallbackInfoReturnable<Biome> cir, int i, int j, int k, Biome biome) {
        this.intBiomeArray[j << 4 | i] = k;
    }

    @ModifyConstant(method = "getBiome", constant = @Constant(intValue = 0xFF))
    private int getBiomeBitmask(int oldValue) {
        return 0xFFFFFFFF;
    }
}
