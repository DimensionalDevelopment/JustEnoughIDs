package org.dimdev.jeid.mixin.core;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.Biomes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Chunk.class)
public class MixinChunk implements INewChunk {
    private final int[] intBiomeArray = generateIntBiomeArray();

    private static int[] generateIntBiomeArray() {
        int[] arr = new int[256];
        Arrays.fill(arr, -1);
        return arr;
    }

    @Override
    public int[] getIntBiomeArray() {
        return intBiomeArray;
    }

    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        System.arraycopy(intBiomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
    }

    @Inject(method = "getBiomeArray", at = @At("HEAD"))
    public void onGetBiomeArray(CallbackInfoReturnable<byte[]> cir) {
        throw new RuntimeException("A mod needs support for extended biome IDs, report to JustEnoughIDs.");
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readBytes([B)Lio/netty/buffer/ByteBuf;", ordinal = 2))
    public ByteBuf readBiomeByteArray(PacketBuffer buf, byte[] dst) {
        setIntBiomeArray(buf.readVarIntArray());
        return buf;
    }

    @Overwrite
    public Biome getBiome(BlockPos pos, BiomeProvider provider) {
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        int index = z << 4 | x;
        int biomeID = intBiomeArray[index];
        Biome biome = Biome.getBiome(biomeID);
        return biome == null ? Biomes.PLAINS : biome;
    }
}
