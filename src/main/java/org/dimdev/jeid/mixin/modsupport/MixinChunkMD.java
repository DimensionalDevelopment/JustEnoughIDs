package org.dimdev.jeid.mixin.modsupport;

import journeymap.client.model.ChunkMD;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(ChunkMD.class)
public abstract class MixinChunkMD {
    @Shadow public abstract World getWorld();
    @Shadow public abstract Chunk getChunk();

    @Overwrite(remap = false)
    @Nullable
    public Biome getBiome(final BlockPos pos) {
        return getChunk().getBiome(pos, getWorld().getBiomeProvider());
    }
}
