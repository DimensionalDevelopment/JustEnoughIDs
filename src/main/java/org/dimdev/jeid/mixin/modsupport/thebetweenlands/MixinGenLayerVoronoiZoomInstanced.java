package org.dimdev.jeid.mixin.modsupport.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thebetweenlands.common.world.gen.layer.GenLayerVoronoiZoomInstanced;

@Pseudo
@Mixin(GenLayerVoronoiZoomInstanced.class)
public abstract class MixinGenLayerVoronoiZoomInstanced {
    @ModifyConstant(method = "func_75904_a", constant = @Constant(intValue = 255), remap = false)
    private int getBitMask(int oldValue) {
        return 0xFFFFFFFF;
    }
}
