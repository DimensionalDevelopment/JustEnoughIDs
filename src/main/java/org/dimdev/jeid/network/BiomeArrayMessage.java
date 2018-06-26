package org.dimdev.jeid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dimdev.jeid.INewChunk;

public class BiomeArrayMessage implements IMessage {
    private int chunkX;
    private int chunkZ;
    private int[] biomeArray;

    public BiomeArrayMessage() {}

    public BiomeArrayMessage(int chunkX, int chunkZ, int[] biomeArray) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.biomeArray = biomeArray;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        chunkX = packetBuffer.readVarInt();
        chunkZ = packetBuffer.readVarInt();
        biomeArray = packetBuffer.readVarIntArray();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.writeVarInt(chunkX);
        packetBuffer.writeVarInt(chunkZ);
        packetBuffer.writeVarIntArray(biomeArray);
    }

    public static class Handler implements IMessageHandler<BiomeArrayMessage, IMessage> {
        @Override
        public IMessage onMessage(BiomeArrayMessage message, MessageContext ctx) {
            Chunk chunk = ctx.getServerHandler().player.world.getChunkFromChunkCoords(message.chunkX, message.chunkZ);
            ((INewChunk) chunk).setIntBiomeArray(message.biomeArray);
            return null;
        }
    }
}
