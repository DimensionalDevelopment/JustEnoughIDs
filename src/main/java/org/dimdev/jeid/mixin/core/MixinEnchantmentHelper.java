package org.dimdev.jeid.mixin.core;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    /** @reason Mojang really likes modifying their original type into something else **/
    @Overwrite
    public static int getEnchantmentLevel(Enchantment enchID, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return 0;
        }
        else
        {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getInteger("id"));

                if (enchantment == enchID)
                {
                    return nbttagcompound.getShort("lvl");
                }
            }

            return 0;
        }
    }

    /** @reason Mojang really likes modifying their original type into something else **/
    @Overwrite
    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack)
    {
        Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newLinkedHashMap();
        NBTTagList nbttaglist = stack.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.getEnchantments(stack) : stack.getEnchantmentTagList();

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getInteger("id"));
            map.put(enchantment, Integer.valueOf(nbttagcompound.getShort("lvl")));
        }

        return map;
    }

    @Overwrite
    public static void setEnchantments(Map<Enchantment, Integer> enchMap, ItemStack stack)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (Map.Entry<Enchantment, Integer> entry : enchMap.entrySet())
        {
            Enchantment enchantment = entry.getKey();

            if (enchantment != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("id", Enchantment.getEnchantmentID(enchantment));
                nbttagcompound.setShort("lvl", entry.getValue().shortValue());
                nbttaglist.appendTag(nbttagcompound);

                if (stack.getItem() == Items.ENCHANTED_BOOK) { ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, entry.getValue().shortValue())); }
            }
        }

        if (nbttaglist.isEmpty())
        {
            if (stack.hasTagCompound())
            {
                stack.getTagCompound().removeTag("ench");
            }
        }
        else if (stack.getItem() != Items.ENCHANTED_BOOK)
        {
            stack.setTagInfo("ench", nbttaglist);
        }
    }

    @Overwrite
    private static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                if (Enchantment.getEnchantmentByID(nbttaglist.getCompoundTagAt(i).getInteger("id")) != null)
                {
                    modifier.calculateModifier(Enchantment.getEnchantmentByID(nbttaglist.getCompoundTagAt(i).getInteger("id")), nbttaglist.getCompoundTagAt(i).getShort("lvl"));
                }
            }
        }
    }

}
