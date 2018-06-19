package org.dimdev.jeid.mixin.core;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
        byte[] arr = new byte[256];
        Arrays.fill(arr, errorBiomeID);
        return arr;
    }

    @Overwrite
    public Biome getBiome(BlockPos pos, BiomeProvider provider) {
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        int index = z << 4 | x;
        int biomeID = intBiomeArray[index];
        Biome biome = Biome.getBiome(biomeID);
        return biome == null ? Biomes.PLAINS : biome;
    }
}
