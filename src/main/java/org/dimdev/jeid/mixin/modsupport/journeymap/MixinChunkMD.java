package org.dimdev.jeid.mixin.modsupport.journeymap;

import journeymap.client.model.ChunkMD;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Pseudo
@Mixin(ChunkMD.class)
public abstract class MixinChunkMD {

    @Shadow public abstract Chunk getChunk();

    /**
     *
     * @param pos
     * @return
     */
    @Overwrite(remap = false)
    @Nullable
    public Biome getBiome(BlockPos pos) {
        Chunk chunk = this.getChunk();
        int[] biomeArray = ((INewChunk) chunk).getIntBiomeArray();
        int biomeId = biomeArray[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF];
        if (biomeId == 0xFFFFFFFF) {
            Biome biome = chunk.getWorld().getBiomeProvider().getBiome(pos, null);

            if (biome == null) {
                return null;
            }
            biomeId = Biome.getIdForBiome(biome);
            biomeArray[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        }

        return Biome.getBiome(biomeId);
    }
}
