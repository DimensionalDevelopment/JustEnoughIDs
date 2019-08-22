package org.dimdev.jeid.mixin.modsupport.creepingnether;

import com.cutievirus.creepingnether.Ref;
import com.cutievirus.creepingnether.entity.CorruptorAbstract;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(CorruptorAbstract.class)
public abstract class MixinCorruptorAbstract {
    @Shadow public abstract Biome getBiome();

    @Overwrite(remap = false)
    public void corruptBiome(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return;
        Biome oldBiome = world.getBiome(pos);
        if (oldBiome == this.getBiome() || oldBiome != Biomes.HELL && this.getBiome() != Ref.biomeCreepingNether) return;
        Chunk chunk = world.getChunk(pos);
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = Biome.getIdForBiome(this.getBiome());
        if (!world.isRemote) {
            MessageManager.CHANNEL.sendToAllAround(
                    new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(this.getBiome())),
                    new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D)
            );
        }
    }
}
