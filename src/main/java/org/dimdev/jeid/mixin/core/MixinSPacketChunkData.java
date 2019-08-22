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

    /** @reason Disable adding biome byte array size. **/
    @Redirect(method = "calculateChunkSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public boolean getIsFullChunk1(SPacketChunkData packet) {
        return false;
    }

    @Inject(method = "calculateChunkSize", at = @At(value = "RETURN"), cancellable = true)
    public void onReturn(Chunk chunkIn, boolean p_189556_2_, int p_189556_3_, CallbackInfoReturnable<Integer> ci) {
        if (this.isFullChunk()) {
            int size = ci.getReturnValue();

            // Now, we add on the actual length of the VarIntArray we're going to be writing in extractChunkData
            size += this.getVarIntArraySize(((INewChunk) chunkIn).getIntBiomeArray());
            ci.setReturnValue(size);
        }
    }

    private int getVarIntArraySize(int[] array) {
        int size = PacketBuffer.getVarIntSize(array.length);
        for (int i: array) {
            size += PacketBuffer.getVarIntSize(i);
        }
        return size;
    }
}
