package org.dimdev.jeid.mixin.modsupport.biomesoplenty;

import biomesoplenty.common.init.ModBiomes;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Pseudo
@Mixin(ModBiomes.class)
public abstract class MixinModBiomes {
    @Shadow private static int nextBiomeId;
    @Shadow private static Set<Integer> idsReservedInConfig;

    @Overwrite
    public static int getNextFreeBiomeId() {
        for (int i = nextBiomeId; i < Integer.MAX_VALUE; ++i) {
            if (Biome.getBiome(i) == null && !idsReservedInConfig.contains(i)) {
                nextBiomeId = i + 1;
                return i;
            }
        }
        throw new RuntimeException("Out of biome IDs (>Integer.MAX_INT), report to JustEnoughIDs");
    }
}
