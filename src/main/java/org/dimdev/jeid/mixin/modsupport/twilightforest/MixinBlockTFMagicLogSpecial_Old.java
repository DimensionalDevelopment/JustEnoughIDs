package org.dimdev.jeid.mixin.modsupport.twilightforest;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import twilightforest.biomes.TFBiomes;
import twilightforest.block.BlockTFMagicLogSpecial;

import java.util.Random;

@Pseudo
@Mixin(BlockTFMagicLogSpecial.class)
public class MixinBlockTFMagicLogSpecial_Old {
    /**
     * For versions upto and including 3.8
     */
    @Overwrite(remap = false)
    private void sendChangedBiome(World world, BlockPos pos) {
        IMessage message = new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(TFBiomes.enchantedForest));
        MessageManager.CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D));
    }

    @Inject(method = "doTreeOfTransformationEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onChangeBiome(World world, BlockPos pos, Random rand, CallbackInfo ci, int i, BlockPos dPos, Biome biomeAt, Chunk chunkAt) {
        ((INewChunk) chunkAt).getIntBiomeArray()[(dPos.getZ() & 15) << 4 | (dPos.getX() & 15)] = Biome.getIdForBiome(TFBiomes.enchantedForest);
    }

}
