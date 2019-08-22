package org.dimdev.jeid.mixin.modsupport.dimstack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkPrimer.class, priority = 500)
public class MixinChunkPrimer {
    public IBlockState ingnoredBlock;

    @Inject(method = "setBlockState", at = @At(value = "HEAD"), cancellable = true)
    private void ignoreBlock(int x, int y, int z, IBlockState state, CallbackInfo ci) {
        if (state != null && this.ingnoredBlock == state) ci.cancel();
    }
}
