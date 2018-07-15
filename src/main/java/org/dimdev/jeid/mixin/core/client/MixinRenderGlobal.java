package org.dimdev.jeid.mixin.core.client;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Shadow @Final private Minecraft mc;
    @Shadow private WorldClient world;

    @Inject(method = "playEvent", at = @At("HEAD"), cancellable = true)
    private void onPlayEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data, CallbackInfo ci) {
        if (type == 2001) {
            IBlockState state = Block.getStateById(data);
            if (state.getMaterial() != Material.AIR) {
                SoundType soundtype = state.getBlock().getSoundType(Block.getStateById(data), world, blockPosIn, null);
                world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
            }
            mc.effectRenderer.addBlockDestroyEffects(blockPosIn, state);

            ci.cancel();
        }
    }
}
