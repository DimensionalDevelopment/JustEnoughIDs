package org.dimdev.jeid.mixin.modsupport.biometweaker;

import me.superckl.biometweaker.util.BiomeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BiomeHelper.class)
public class MixinBiomeHelper {
    @ModifyConstant(method = "getNextFreeBiomeId", constant = @Constant(intValue = 0xFF), remap = false)
    private static int getMaxBiomeId(int oldValue) {
        return 0xFFFFFFFF;
    }

    @ModifyConstant(method = "getNextFreeBiomeId", constant = @Constant(intValue = 0x100), remap = false)
    private static int getLoopUpperLimit(int oldValue) {
        return 0x7FFFFFFF;
    }
}
