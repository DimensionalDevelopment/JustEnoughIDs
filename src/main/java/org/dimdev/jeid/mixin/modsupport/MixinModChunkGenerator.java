package org.dimdev.jeid.mixin.modsupport;

import biomesoplenty.common.world.ChunkGeneratorHellBOP;
import biomesoplenty.common.world.ChunkGeneratorOverworldBOP;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin({ChunkGeneratorOverworldBOP.class,
        ChunkGeneratorHellBOP.class})
@SuppressWarnings("UnresolvedMixinReference")
public class MixinModChunkGenerator {
    private Biome[] temporaryBiomes;

    /** @reason Return an empty biome byte array if the chunk is using an int biome array. **/
    @Redirect(method = "func_185932_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B"))
    private byte[] getBiomeArray(Chunk chunk) {
        INewChunk newChunk = (INewChunk) chunk;
        int[] intBiomeArray = newChunk.getIntBiomeArray();
        for (int i = 0; i < intBiomeArray.length; ++i) {
            intBiomeArray[i] = Biome.getIdForBiome(temporaryBiomes[i]);
        }
        temporaryBiomes = null;
        return new byte[0];
    }

    @Redirect(method = "func_185932_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeProvider;getBiomes([Lnet/minecraft/world/biome/Biome;IIII)[Lnet/minecraft/world/biome/Biome;"))
    private Biome[] getBiomes(BiomeProvider biomeProvider, Biome[] oldBiomeList, int x, int z, int width, int depth) {
        temporaryBiomes = biomeProvider.getBiomes(oldBiomeList, x, z, width, depth);
        return temporaryBiomes;
    }
}
