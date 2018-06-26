package org.dimdev.jeid.mixin.modsupport;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import twilightforest.biomes.TFBiomes;
import twilightforest.block.BlockTFMagicLogSpecial;

@Pseudo
@Mixin(BlockTFMagicLogSpecial.class)
public class MixinBlockTFMagicLogSpecial {
    @Overwrite
    private void sendChangedBiome(World world, BlockPos pos) {
        IMessage message = new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(TFBiomes.enchantedForest));
        MessageManager.CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D));
    }
}
