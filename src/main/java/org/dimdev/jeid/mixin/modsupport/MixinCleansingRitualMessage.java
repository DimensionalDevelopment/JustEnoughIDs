package org.dimdev.jeid.mixin.modsupport;

import com.shinoow.abyssalcraft.common.network.client.CleansingRitualMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CleansingRitualMessage.class)
public class MixinCleansingRitualMessage {
    @Shadow private int x;
    @Shadow private int z;
    @Shadow private int biomeID;

    @Overwrite(remap = false)
    public void process(EntityPlayer player, Side side) {
        Chunk chunk = player.world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
        int chunkX = x & 0xF;
        int chunkZ = z & 0xF;
        ((INewChunk) chunk).getIntBiomeArray()[chunkZ << 4 | chunkX ] = biomeID;
        Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(x - 7, 0, z - 7, x + 7, 255, z + 7);
    }
}
