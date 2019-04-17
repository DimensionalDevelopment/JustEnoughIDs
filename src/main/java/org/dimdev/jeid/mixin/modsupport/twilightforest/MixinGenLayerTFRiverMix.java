package org.dimdev.jeid.mixin.modsupport.twilightforest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import twilightforest.world.layer.GenLayerTFRiverMix;

@Pseudo
@Mixin(GenLayerTFRiverMix.class)
public class MixinGenLayerTFRiverMix {
    @ModifyConstant(method = "func_75904_a", constant = @Constant(intValue = 255), remap = false)
    private int getBitMask(int oldValue) {
        return 0xFFFFFFFF;
    }
}
