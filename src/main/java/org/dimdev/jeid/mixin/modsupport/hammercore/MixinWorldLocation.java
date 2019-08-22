package org.dimdev.jeid.mixin.modsupport.hammercore;

import com.zeitheron.hammercore.utils.WorldLocation;
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
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldLocation.class)
public class MixinWorldLocation {
    @Shadow private World world;
    @Shadow private BlockPos pos;

    /**
     *
     * @author sk2048
     */
    @Overwrite(remap = false)
    public void setBiome(Biome biome) {
        Chunk chunk = this.world.getChunk(this.pos);
        ((INewChunk) chunk).getIntBiomeArray()[(this.pos.getZ() & 0xF) << 4 | this.pos.getX() & 0xF] = Biome.getIdForBiome(biome);
        chunk.markDirty();
        if (!this.world.isRemote) {
            MessageManager.CHANNEL.sendToAllAround(
                    new BiomeChangeMessage(this.pos.getX(), this.pos.getZ(), Biome.getIdForBiome(biome)),
                    new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), 128.0D, this.pos.getZ(), 128.0D)
            );
        }
    }
}
