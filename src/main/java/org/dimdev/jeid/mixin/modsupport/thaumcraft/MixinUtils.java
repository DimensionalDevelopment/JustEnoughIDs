package org.dimdev.jeid.mixin.modsupport.thaumcraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import thaumcraft.common.lib.utils.Utils;

@Pseudo
@Mixin(Utils.class)
public class MixinUtils {
    @Overwrite
    public static void setBiomeAt(World world, BlockPos pos, Biome biome, boolean sync) {
        if (biome == null) return;

        INewChunk newChunk = (INewChunk) world.getChunk(pos);
        int[] array = newChunk.getIntBiomeArray();
        array[(pos.getX() & 15) << 4 | pos.getZ() & 15] = Biome.getIdForBiome(biome) & 255;
        newChunk.setIntBiomeArray(array);

        if (sync && !world.isRemote) {
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), world.getHeight(pos).getY(), pos.getZ(), 32.0D);
            MessageManager.CHANNEL.sendToAllAround(new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome)), point);
        }
    }
}
