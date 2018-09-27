package org.dimdev.jeid.mixin.core;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SPacketRemoveEntityEffect.class)
public class MixinSPacketRemoveEntityEffect {

    @Shadow private int entityId;
    @Shadow private Potion effectId;

    @Overwrite
    public void readPacketData(PacketBuffer buf) {
        this.entityId = buf.readVarInt();
        this.effectId = Potion.getPotionById(buf.readInt());
    }

    @Overwrite
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(this.entityId);
        buf.writeInt(Potion.getIdFromPotion(this.effectId));
    }

}
