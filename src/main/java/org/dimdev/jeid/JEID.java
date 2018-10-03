package org.dimdev.jeid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.network.MessageManager;

import java.util.Random;

@Mod(modid = "jeid",
     name = "JustEnoughIDs",
     updateJSON = "https://gist.githubusercontent.com/Runemoro/67b1d8d31af58e9d35410ef60b2017c3/raw/1fe08a6c45a1f481a8a2a8c71e52d4245dcb7713/jeid_update.json")
public class JEID {
    private static final boolean DEBUG_BLOCK_IDS = false;
    private static final boolean DEBUG_ITEM_IDS = false;
    private static final boolean DEBUG_BIOME_IDS = false;
    private static final boolean DEBUG_POTION_IDS = false;
    private static final boolean DEBUG_ENCHANT_IDS = false;
    public static final Biome errorBiome = new BiomeVoid(new Biome.BiomeProperties("A mod doesn't support extended biome IDs -- report to JEID"))
            .setRegistryName("jeid:error_biome");

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // Register messages
        MessageManager.init();

        // Error Biome Registration
        GameRegistry.findRegistry(Biome.class).register(errorBiome);

        // Debug code
        if (DEBUG_BLOCK_IDS) {
            IForgeRegistry<Block> blockRegistry = GameRegistry.findRegistry(Block.class);
            IForgeRegistry<Item> itemRegistry = GameRegistry.findRegistry(Item.class);
            for (int i = 0; i < 5000; i++) {
                Block block = new Block(Material.GROUND)
                        .setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
                        .setRegistryName("block_" + i)
                        .setRegistryName(new ResourceLocation("jeid:block_" + i));

                blockRegistry.register(block);
                itemRegistry.register(new ItemBlock(block).setRegistryName(new ResourceLocation("jeid:block_" + i)));
            }
        }

        if (DEBUG_ITEM_IDS) {
            IForgeRegistry<Item> itemRegistry = GameRegistry.findRegistry(Item.class);
            for (int i = 0; i < 40000; i++) {
                Item item = new Item()
                        .setCreativeTab(CreativeTabs.FOOD)
                        .setRegistryName("item_" + i)
                        .setRegistryName(new ResourceLocation("jeid:item_" + i));

                itemRegistry.register(item);
            }
        }

        if (DEBUG_BIOME_IDS) {
            IForgeRegistry<Biome> biomeRegistry = GameRegistry.findRegistry(Biome.class);
            for (int i = 0; i < 300; i++) {
                Biome biome = new Biome(new Biome.BiomeProperties("Biome " + i)) {}
                        .setRegistryName(new ResourceLocation("jeid:biome_" + i));

                biomeRegistry.register(biome);
            }
        }

        if (DEBUG_POTION_IDS) {
            IForgeRegistry<Potion> potionRegistry = GameRegistry.findRegistry(Potion.class);
            IForgeRegistry<PotionType> potionTypeRegistry = GameRegistry.findRegistry(PotionType.class);
            for (int i = 0; i < 300; i++) {
                Potion potion = new PotionTest(i).setRegistryName(new ResourceLocation("jeid:potion_" + i));
                potionRegistry.register(potion);
            }

            for (int i = 0; i < 300; i++) {
                PotionType pt = new PotionType(new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("jeid:potion_" + i)),
                                        2000,
                                        0,
                                        false,
                                    true));
                pt.setRegistryName(new ResourceLocation("jeid:potiontype_"+i));
                potionTypeRegistry.register(pt);
            }
        }

        if (DEBUG_ENCHANT_IDS) {
            IForgeRegistry<Enchantment> enchantRegistry = GameRegistry.findRegistry(Enchantment.class);
            for (int i = 0; i < Short.MAX_VALUE; i++) {
                Enchantment ench = new EnchantTest(i).setRegistryName(new ResourceLocation("jeid:enchant_"+i));

                enchantRegistry.register(ench);
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        JEIDTransformer.REGISTRY = net.minecraftforge.registries.GameData.getWrapper(Potion.class);
    }

    public static class PotionTest extends Potion {

        private static final Random r = new Random();
        private String nm = "";

        protected PotionTest(int id) {
            super(false, 0xFFFFFF & r.nextInt(Integer.MAX_VALUE));
            nm = "Test Potion #"+id;
        }

        @Override
        public String getName() {
            return nm;
        }
    }

    public static class EnchantTest extends Enchantment {

        public EnchantTest(int i)
        {
            super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[EntityEquipmentSlot.CHEST.getIndex()]);
            this.setName("Test Enchantment #"+i);
        }

    }
}