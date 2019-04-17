package org.dimdev.jeid.mixin.modsupport.abyssalcraft;

import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.CleansingRitualMessage;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(BiomeUtil.class)
public class MixinBiomeUtil {
    @Overwrite(remap = false)
    public static void updateBiome(World worldIn, BlockPos pos, int b, boolean batched) {
        Chunk c = worldIn.getChunk(pos);
        ((INewChunk) c).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = b;
        c.setModified(true);
        if(!worldIn.isRemote) {
            PacketDispatcher.sendToDimension(new CleansingRitualMessage(pos.getX(), pos.getZ(), b, batched), worldIn.provider.getDimension());
        }
    }
}
