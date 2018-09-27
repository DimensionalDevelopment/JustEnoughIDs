package org.dimdev.jeid.mixin.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow private Minecraft client;
    @Shadow private WorldClient world;

    @Overwrite
    public void handleEntityEffect(SPacketEntityEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayClient)(Object)this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity instanceof EntityLivingBase)
        {
            Potion potion = Potion.getPotionById(packetIn.getEffectId() & 0xFFFF);

            if (potion != null)
            {
                PotionEffect potioneffect = new PotionEffect(potion, packetIn.getDuration(), packetIn.getAmplifier(), packetIn.getIsAmbient(), packetIn.doesShowParticles());
                potioneffect.setPotionDurationMax(packetIn.isMaxDuration());
                ((EntityLivingBase)entity).addPotionEffect(potioneffect);
            }
        }
    }

}
