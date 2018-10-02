package org.dimdev.jeid.mixin.core;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Copy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SPacketEntityEffect.class)
public class MixinSPacketEntityEffect {

    private int effectId;

    @Shadow private int entityId;
    @Shadow private byte amplifier;
    @Shadow private int duration;
    @Shadow private byte flags;

    @Copy
    public MixinSPacketEntityEffect(int entityIdIn, PotionEffect effect) {
        this.entityId = entityIdIn;
        this.effectId = (getIdFromPotEffect(effect) & 0xFFFF);
        this.amplifier = (byte)(effect.getAmplifier() & 255);

        if (effect.getDuration() > 32767)
        {
            this.duration = 32767;
        }
        else
        {
            this.duration = effect.getDuration();
        }

        this.flags = 0;

        if (effect.getIsAmbient())
        {
            this.flags = (byte)(this.flags | 1);
        }

        if (effect.doesShowParticles())
        {
            this.flags = (byte)(this.flags | 2);
        }
    }

    /** @reason Modifies packet to read an int rather than byte. **/
    @Overwrite
    public void readPacketData(PacketBuffer buf) {
        this.entityId = buf.readVarInt();
        this.effectId = buf.readVarInt();
        this.amplifier = buf.readByte();
        this.duration = buf.readVarInt();
        this.flags = buf.readByte();
    }

    /** @reason Modifies packet to write an int rather than byte. **/
    @Overwrite
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.effectId);
        buf.writeByte(this.amplifier);
        buf.writeVarInt(this.duration);
        buf.writeByte(this.flags);
    }

    @Redirect(method = "getEffectId", at = @At("RETURN"))
    @SideOnly(Side.CLIENT)
    public int getEffectId()
    {
        return this.effectId;
    }

    public int getIdFromPotEffect(PotionEffect pe) {
        return Potion.getIdFromPotion(pe.getPotion());
    }

}
