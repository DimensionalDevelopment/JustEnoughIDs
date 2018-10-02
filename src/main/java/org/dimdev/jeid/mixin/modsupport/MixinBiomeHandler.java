package org.dimdev.jeid.mixin.modsupport;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import zmaster587.advancedRocketry.util.BiomeHandler;

@Pseudo
@Mixin(BiomeHandler.class)
public class MixinBiomeHandler {
    @Overwrite(remap = false)
    public static void changeBiome(World world, int biomeId, BlockPos pos) {
        changeBiome(world, biomeId, world.getChunk(pos), pos);
    }

    @Overwrite(remap = false)
    public static void changeBiome(World world, int biomeId, Chunk chunk, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Biome biomeTo = Biome.getBiome(biomeId);

        if (biome == biomeTo) {
            return;
        }

        int x = pos.getX();
        int z = pos.getZ();

        if (biome.topBlock != biomeTo.topBlock) {
            int topBlockY = chunk.getHeightValue(x & 15, z & 15) - 1;

            while (!world.getBlockState(new BlockPos(x, topBlockY, z)).isOpaqueCube() && topBlockY > 0) topBlockY--;
            if (topBlockY == 0) return;

            if (chunk.getBlockState(x & 15, topBlockY, z & 15) == biome.topBlock) {
                chunk.setBlockState(new BlockPos(x & 15, topBlockY, z & 15), biomeTo.topBlock);
            }
        }

        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = biomeId;

        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 300);
        MessageManager.CHANNEL.sendToAllAround(new BiomeChangeMessage(pos.getX(), pos.getY(), biomeId), point);
    }
}
