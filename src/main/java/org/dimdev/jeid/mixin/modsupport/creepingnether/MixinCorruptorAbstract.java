package org.dimdev.jeid.mixin.modsupport.creepingnether;

import com.cutievirus.creepingnether.entity.CorruptorAbstract;
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
@Mixin(CorruptorAbstract.class)
public class MixinCorruptorAbstract {
    @Shadow private static Biome toBiome;

    /**
     * This should(?) fix Creeping Nether issues
     */
    @Overwrite(remap = false)
    public static void corruptBiome(World world, BlockPos pos) {
        if (world.isBlockLoaded(pos)) {
            Chunk chunk = world.getChunk(pos);
            ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = Biome.getIdForBiome(toBiome);
        }
    }
}
