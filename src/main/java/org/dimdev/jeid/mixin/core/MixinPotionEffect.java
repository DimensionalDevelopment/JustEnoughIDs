package org.dimdev.jeid.mixin.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PotionEffect.class)
public class MixinPotionEffect {

    @Shadow public Potion getPotion() { return null; }
    @Shadow public int getAmplifier() { return -1; }
    @Shadow public int getDuration() { return -1; }
    @Shadow public boolean getIsAmbient() { return false; }
    @Shadow public boolean doesShowParticles() { return false; }
    @Shadow private void writeCurativeItems(NBTTagCompound nbt) {}
    @Shadow private static PotionEffect readCurativeItems(PotionEffect effect, NBTTagCompound nbt) { return null; }

    /** @reason Modifies NBT writing to make it write ints over bytes **/
    @Overwrite
    public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Id", (Potion.getIdFromPotion(this.getPotion())& 0xFFFF));
        nbt.setByte("Amplifier", (byte)this.getAmplifier());
        nbt.setInteger("Duration", this.getDuration());
        nbt.setBoolean("Ambient", this.getIsAmbient());
        nbt.setBoolean("ShowParticles", this.doesShowParticles());
        writeCurativeItems(nbt);
        return nbt;
    }

    /** @reason Modifies NBT reading to make it read ints over bytes and makes code more intuitive **/
    @Overwrite
    public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound nbt) {
        Potion potion = Potion.getPotionById(nbt.getInteger("Id") & 0xFFFF);

        if (potion == null) {
            return null;
        } else {
            boolean flag1 = true;

            if (nbt.hasKey("ShowParticles", 1))
            {
                flag1 = nbt.getBoolean("ShowParticles");
            }

            return readCurativeItems(
                    new PotionEffect(potion,
                            nbt.getInteger("Duration"),
                            nbt.getByte("Amplifier") < 0 ? 0 : nbt.getByte("Amplifier"),
                            nbt.getBoolean("Ambient"),
                            flag1),
                    nbt);
        }
    }
}
