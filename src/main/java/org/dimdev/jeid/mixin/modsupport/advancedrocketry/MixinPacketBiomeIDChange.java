package org.dimdev.jeid.mixin.modsupport.advancedrocketry;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.libVulpes.util.HashedBlockPosition;

@Pseudo
@Mixin(PacketBiomeIDChange.class)
public class MixinPacketBiomeIDChange {
    @Shadow
    Chunk chunk;
    @Shadow
    int worldId, xPos, zPos;
    @Shadow
    HashedBlockPosition pos;

    int[] intArray;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        intArray = new int[256];
    }

    /**
     * @author sk2048
     */
    @Overwrite(remap = false)
    public void write(ByteBuf out) {
        out.writeInt(worldId);
        out.writeInt(chunk.x);
        out.writeInt(chunk.z);
        out.writeInt(pos.x);
        out.writeShort(pos.y);
        out.writeInt(pos.z);

        for (int biomeId : ((INewChunk) chunk).getIntBiomeArray()) {
            out.writeInt(biomeId);
        }
    }

    /**
     * @author sk2048
     */
    @Overwrite(remap = false)
    public void readClient(ByteBuf in) {
        worldId = in.readInt();
        xPos = in.readInt();
        zPos = in.readInt();

        pos.x = in.readInt();
        pos.y = in.readShort();
        pos.z = in.readInt();

        for (int i = 0; i < 256; i++) {
            int biomeId = in.readInt();
            intArray[i] = biomeId;
        }
    }


    /**
     * @author sk2048
     */
    @SideOnly(Side.CLIENT)
    @Overwrite(remap = false)
    public void executeClient(EntityPlayer thePlayer) {
        if (thePlayer.world.provider.getDimension() == worldId) {
            chunk = thePlayer.world.getChunk(xPos, zPos);
            if (chunk.isLoaded()) {
                ((INewChunk) chunk).setIntBiomeArray(intArray);
                chunk.markDirty();
                thePlayer.world.markBlockRangeForRenderUpdate(pos.getBlockPos(), pos.getBlockPos());

                if (Minecraft.getMinecraft().gameSettings.particleSetting < 2)
                    AdvancedRocketry.proxy.spawnParticle("smallLazer", thePlayer.world, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }
}
