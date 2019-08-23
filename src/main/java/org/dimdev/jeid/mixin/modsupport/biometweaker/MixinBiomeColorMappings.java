package org.dimdev.jeid.mixin.modsupport.biometweaker;

import me.superckl.biometweaker.util.BiomeColorMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BiomeColorMappings.class)
public class MixinBiomeColorMappings {
    @ModifyConstant(method = "getColorForBiome", constant = @Constant(intValue = 0xFF), remap = false)
    private static int getBitMask(int oldValue) {
        return 0xFFFFFFFF;
    }
}
