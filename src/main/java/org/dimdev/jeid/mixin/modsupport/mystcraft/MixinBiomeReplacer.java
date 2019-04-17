package org.dimdev.jeid.mixin.modsupport.mystcraft;

import com.xcompwiz.mystcraft.symbol.symbols.SymbolFloatingIslands;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Pseudo
@Mixin(SymbolFloatingIslands.BiomeReplacer.class)
public class MixinBiomeReplacer {
    @Shadow private HashMap<List<Integer>, boolean[]> chunks;
    @Shadow private Biome biome;

    @Overwrite(remap = false)
    public void finalizeChunk(Chunk chunk, int chunkX, int chunkZ) {
        boolean[] modified = chunks.remove(Arrays.asList(chunkX, chunkZ));

        if (modified != null) {
            int[] biomes = ((INewChunk) chunk).getIntBiomeArray();

            for(int coords = 0; coords < modified.length; ++coords) {
                if (modified[coords]) {
                    biomes[coords] = Biome.getIdForBiome(biome);
                }
            }
        }
    }
}
