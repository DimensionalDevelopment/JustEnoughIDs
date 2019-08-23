package org.dimdev.jeid.mixin.modsupport.biometweaker;

import me.superckl.biometweaker.server.command.CommandSetBiome;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.Utils;
import org.dimdev.jeid.network.BiomeArrayMessage;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;

@Mixin(CommandSetBiome.class)
public class MixinCommandSetBiome {
    @Inject(method = "func_184881_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;func_76605_m()[B", remap = false), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void setBiomeArrayElement(MinecraftServer server, ICommandSender sender, String args[], CallbackInfo ci, BlockPos coord, World world, Biome gen, Integer i, int id, boolean blocks, int count, int x, int z, int realX, int realZ, Chunk chunk) {
        Utils.LOGGER.info("setting biome at {}, {}", x, z);
        ((INewChunk) chunk).getIntBiomeArray()[(z & 0xF) << 4 | x & 0xF] = id;
        MessageManager.CHANNEL.sendToAllTracking(new BiomeChangeMessage(x, z, id), new NetworkRegistry.TargetPoint(world.provider.getDimension(), coord.getX(), coord.getY(), coord.getZ(), 256));
    }

    @Inject(method = "func_184881_a", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;fill([BB)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void setBiomeArray(MinecraftServer server, ICommandSender sender, String args[], CallbackInfo ci, BlockPos coord, World world, Biome gen, Integer i, int id, boolean blocks, int count, byte biomeArray[]) {
        final int[] intBiomeArray = new int[256];
        Arrays.fill(intBiomeArray, id);
        ChunkPos chunkPos = new ChunkPos(coord);

        for (int x = chunkPos.x - i; x <= chunkPos.x + i; x++) {
            for (int z = chunkPos.z - i; z <= chunkPos.z + i; z++) {
                ((INewChunk) world.getChunk(x, z)).setIntBiomeArray(Arrays.copyOf(intBiomeArray, intBiomeArray.length));
                MessageManager.CHANNEL.sendToAllTracking(new BiomeArrayMessage(x, z, Arrays.copyOf(intBiomeArray, intBiomeArray.length)), new NetworkRegistry.TargetPoint(world.provider.getDimension(), coord.getX(), coord.getY(), coord.getZ(), 256));
                count++;
            }
        }

        sender.sendMessage(new TextComponentTranslation("biometweaker.msg.setbiome.chunksuccess.text", count, gen.getBiomeName()).setStyle(new Style().setColor(TextFormatting.GOLD)));

        ci.cancel();
    }
}
