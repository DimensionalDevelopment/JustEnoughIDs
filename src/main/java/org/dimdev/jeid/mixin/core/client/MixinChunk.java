package org.dimdev.jeid.mixin.core.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class MixinChunk implements INewChunk {
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readBytes([B)Lio/netty/buffer/ByteBuf;", ordinal = 2))
    private ByteBuf readBiomeByteArray(PacketBuffer buf, byte[] dst) {
        setIntBiomeArray(buf.readVarIntArray());
        return buf;
    }
}
