package org.dimdev.jeid.mixin.core;

import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GenLayerVoronoiZoom.class)
public abstract class MixinGenLayerVoronoiZoom {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return 0xFFFFFFFF;
    }
}
