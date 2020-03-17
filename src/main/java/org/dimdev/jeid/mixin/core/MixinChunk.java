package org.dimdev.jeid.mixin.core;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(Chunk.class)
public class MixinChunk implements INewChunk {


    @Shadow @Final private World world;

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

    /**
     * @author Unknown
     */
    @Overwrite
    public byte[] getBiomeArray() {
        byte[] arr = new byte[256];
        Arrays.fill(arr, errorBiomeID);
        return arr;
    }

    /**
     * @author Clienthax
     * @reason No way to modify locals in the manner we need currently..
     */
    @Overwrite
    public Biome getBiome(BlockPos pos, BiomeProvider provider)
    {
        int i = pos.getX() & 15;
        int j = pos.getZ() & 15;
        //JEID START
        int k = this.intBiomeArray[j << 4 | i];
        //JEID END

        if (k == 255)
        {
            // Forge: checking for client ensures that biomes are only generated on integrated server
            // in singleplayer. Generating biomes on the client may corrupt the biome ID arrays on
            // the server while they are being generated because IntCache can't be thread safe,
            // so client and server may end up filling the same array.
            // This is not necessary in 1.13 and newer versions.
            Biome biome = world.isRemote ? Biomes.PLAINS : provider.getBiome(pos, Biomes.PLAINS);
            k = Biome.getIdForBiome(biome);
            //JEID START
            this.intBiomeArray[j << 4 | i] = k;
            //JEID END
        }

        Biome biome1 = Biome.getBiome(k);
        return biome1 == null ? Biomes.PLAINS : biome1;
    }

}
