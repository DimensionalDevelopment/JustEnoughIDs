package org.dimdev.jeid.mixin.modsupport.thebetweenlands;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.common.block.terrain.BlockSpreadingDeath;

@Pseudo
@Mixin(BlockSpreadingDeath.class)
public class MixinBlockSpreadingDeath {
    @Inject(method = "convertBiome", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertBiomes(World world, BlockPos pos, Biome biome, CallbackInfo ci) {
        Chunk chunk = world.getChunk(pos);
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = Biome.getIdForBiome(biome);
        chunk.markDirty();
        ci.cancel();
    }
}
