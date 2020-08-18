package org.dimdev.jeid.mixin.modsupport.bewitchment;

import com.bewitchment.common.item.tool.ItemBoline;
import com.bewitchment.common.ritual.RitualBiomeShift;
import com.bewitchment.common.world.BiomeChangingUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(RitualBiomeShift.class)
public class MixinRitualBiomeShift {
    @Overwrite(remap = false)
    public void onFinished(World world, BlockPos altarPos, BlockPos effectivePos, EntityPlayer caster, ItemStackHandler inventory) {
        int id;
        Chunk chunk = world.getChunk(effectivePos);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBoline) {
                id = stack.getTagCompound().getInteger("biome_id");

                //might run thru that only server side, since all client change is done with packets afterwards
                int radius = 32; //maybe change that depending on some other stuff?
                for (double x = -radius; x < radius; x++) {
                    for (double z = -radius; z < radius; z++) {
                        if (Math.sqrt((x * x) + (z * z)) < radius) {
                            BlockPos pos = effectivePos.add(x, 0, z);
                            BiomeChangingUtils.setBiome(world, Biome.getBiomeForId(id), pos);
                            chunk.getWorld().getMinecraftServer().getPlayerList().getPlayers().forEach(p -> p.connection.sendPacket(new SPacketChunkData(chunk, 65535)));
                            for (i = 0; i < inventory.getSlots(); i++) {
                                inventory.extractItem(i, 1, false);
                            }
                        }
                    }
                }
            }
        }
    }
}
