package org.dimdev.jeid.mixin.modsupport.geographicraft;

import climateControl.DimensionManager;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(DimensionManager.class)
public class MixinDimensionManager {
    @Overwrite(remap = false)
    private boolean hasOnlySea(Chunk tested) {
        for (int biome : ((INewChunk) tested).getIntBiomeArray()) {
            if (biome != 0 && biome != Biome.getIdForBiome(Biomes.DEEP_OCEAN)) {
                return false;
            }
        }

        return true;
    }
}
