package org.dimdev.jeid.mixin.modsupport;

import com.cutievirus.creepingnether.entity.EntityPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(EntityPortal.class)
public class MixinEntityPortal {
    @Shadow private static Biome toBiome;

    @Overwrite(remap = false)
    public static void corruptBiome(World world, BlockPos pos) {
        if (world.isBlockLoaded(pos)) {
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = Biome.getIdForBiome(toBiome);
        }
    }
}
