package org.dimdev.jeid.mixin.modsupport.bookshelf;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.WorldUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.Arrays;

@Pseudo
@Mixin(WorldUtils.class)
public abstract class MixinWorldUtils {
    @Overwrite(remap = false)
    public static void setBiomes(World world, BlockPos pos, Biome biome) {
        try {
            final Chunk chunk = world.getChunk(pos);
            final int[] biomes = ((INewChunk) chunk).getIntBiomeArray();
            Arrays.fill(biomes, Biome.getIdForBiome(biome));

            WorldUtils.updateNearbyChunks(world, chunk, true, true);
        } catch (Exception e) {
            Constants.LOG.warn(e, "Unable to set biome for Pos: {}, Biome: {}", pos.toString(), biome.getRegistryName());
        }
    }
}
