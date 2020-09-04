package org.dimdev.jeid.mixin.modsupport.bewitchment;

import com.bewitchment.common.world.BiomeChangingUtils;
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

@Pseudo
@Mixin(BiomeChangingUtils.class)
public class MixinUtils {
    @Overwrite
    public static void setBiome(World world, Biome biome, BlockPos pos) {
        Chunk chunk = world.getChunk(pos);
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = Biome.getIdForBiome(biome);
        chunk.markDirty();

        if (!world.isRemote) {
            MessageManager.CHANNEL.sendToAllAround(
                    new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome)),
                    new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D)
            );
        }
    }
}
