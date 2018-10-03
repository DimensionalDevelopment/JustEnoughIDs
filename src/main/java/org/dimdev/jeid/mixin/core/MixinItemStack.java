package org.dimdev.jeid.mixin.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Shadow private NBTTagCompound stackTagCompound;
    @Shadow public void setTagCompound(@Nullable NBTTagCompound nbt)
    {
        this.stackTagCompound = nbt;
    }

    /** @reason because Mojang can't just send an Integer when the ID is already an Interger **/
    @Overwrite
    public void addEnchantment(Enchantment ench, int level)
    {
        if (this.stackTagCompound == null) { this.setTagCompound(new NBTTagCompound()); }

        if (!this.stackTagCompound.hasKey("ench", 9)) { this.stackTagCompound.setTag("ench", new NBTTagList()); }

        NBTTagList nbttaglist = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setInteger("id", Enchantment.getEnchantmentID(ench));
        nbttagcompound.setShort("lvl", (short)((byte)level));
        nbttaglist.appendTag(nbttagcompound);
    }
}
