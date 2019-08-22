package org.dimdev.jeid.mixin.modsupport.advancedrocketry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.advancedRocketry.util.BiomeHandler;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

@Pseudo
@Mixin(BiomeHandler.class)
public class MixinBiomeHandler {
    /**
     * @author sk2048
     */
    @Overwrite
    public static void changeBiome(World world, int biomeId, BlockPos pos) {
        Chunk chunk = world.getChunk(pos);

        Biome biome = world.getBiome(pos);
        Biome biomeTo = Biome.getBiome(biomeId);

        if (biome == biomeTo)
            return;

        if (biome.topBlock != biomeTo.topBlock) {
            BlockPos yy = world.getHeight(pos);

            for (; !world.getBlockState(yy).isOpaqueCube() || yy.getY() < 0; yy = yy.down()) ;

            if (world.getBlockState(yy) == biome.topBlock)
                world.setBlockState(yy, biomeTo.topBlock);
        }

        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        chunk.markDirty();

        PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new HashedBlockPosition(pos)), world.provider.getDimension(), pos, 256);
    }

    /**
     * @author sk2048
     */
    @Overwrite
    public static void changeBiome(World world, int biomeId, Chunk chunk, BlockPos pos) {
        changeBiome(world, biomeId, pos);
    }
}
