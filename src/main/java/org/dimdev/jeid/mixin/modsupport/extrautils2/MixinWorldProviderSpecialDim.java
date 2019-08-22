package org.dimdev.jeid.mixin.modsupport.extrautils2;

import com.rwtema.extrautils2.dimensions.workhousedim.WorldProviderSpecialDim;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;

@Pseudo
@Mixin(WorldProviderSpecialDim.class)
public class MixinWorldProviderSpecialDim {
    @Inject(method = "generate", at = @At(target = "Lnet/minecraft/world/chunk/Chunk;setTerrainPopulated(Z)V", value = "INVOKE"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void setChunkIntBiomeArray(int x, int z, ChunkProviderServer chunkProvider, Object generator, CallbackInfo ci, Chunk chunk, int idForBiome) {
        Arrays.fill(((INewChunk) chunk).getIntBiomeArray(), idForBiome);
    }
}
