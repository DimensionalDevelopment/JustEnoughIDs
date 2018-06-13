package org.dimdev.jeid.mixin.core;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SPacketChunkData.class)
public abstract class MixinSPacketChunkData {
    @Shadow public abstract boolean isFullChunk();

    /** @reason Write the biome int array. **/
    @Inject(method = "extractChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public void writeBiomeArray(PacketBuffer buf, Chunk chunk, boolean writeSkylight, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
        if (isFullChunk()) {
            buf.writeVarIntArray(((INewChunk) chunk).getIntBiomeArray());
        }
    }

    /** @reason Disable writing biome byte array. **/
    @Redirect(method = "extractChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public boolean getIsFullChunk(SPacketChunkData packet) {
        return false;
    }

    @Redirect(method = "calculateChunkSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B"))
    private byte[] getBiomeArray(Chunk chunk) {
        INewChunk newChunk = (INewChunk) chunk;
        return new byte[newChunk.getIntBiomeArray().length * 4];
    }
}
