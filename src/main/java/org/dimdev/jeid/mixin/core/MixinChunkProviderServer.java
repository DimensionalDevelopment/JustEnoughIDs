package org.dimdev.jeid.mixin.core;

import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {
    @Shadow @Final public WorldServer world;
    private Biome[] reusableBiomeList = new Biome[256];

    /** @reason Return an empty biome byte array if the chunk is using an int biome array. **/
    @Redirect(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;generateChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk generateChunk(IChunkGenerator generator, int x, int z) {
        Chunk chunk = generator.generateChunk(x, z);
        Biome[] biomes = world.getBiomeProvider().getBiomes(reusableBiomeList, x * 16, z * 16, 16, 16);

        INewChunk newChunk = (INewChunk) chunk;
        int[] intBiomeArray = newChunk.getIntBiomeArray();
        for (int i = 0; i < intBiomeArray.length; ++i) {
            intBiomeArray[i] = Biome.getIdForBiome(biomes[i]);
        }
        return chunk;
    }
}
