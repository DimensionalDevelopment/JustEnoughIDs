package org.dimdev.jeid.mixin.core;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.*;

/** Rewrite most of the class to support an unlimited number of IDs (map rather than array). **/
@Mixin(value = StatList.class, priority = 500)
public final class MixinStatList {
    @Shadow @Final protected static Map<String, StatBase> ID_TO_STAT_MAP;
    @Shadow @Final public static List<StatBase> BASIC_STATS;
    @Shadow @Final public static List<StatCrafting> USE_ITEM_STATS;
    @Shadow @Final public static List<StatCrafting> MINE_BLOCK_STATS;

    @Shadow @Final public static List<StatBase> ALL_STATS;
    private static final Map<Block, StatBase> BLOCK_STAT_MAP = new HashMap<>();
    private static final Map<Item, StatBase> CRAFTS_STATS_MAP = new HashMap<>();
    private static final Map<Item, StatBase> OBJECT_USE_STATS_MAP = new HashMap<>();
    private static final Map<Item, StatBase> OBJECT_BREAK_STATS_MAP = new HashMap<>();
    private static final Map<Item, StatBase> OBJECTS_PICKED_UP_STATS_MAP = new HashMap<>();
    private static final Map<Item, StatBase> OBJECTS_DROPPED_STATS_MAP = new HashMap<>();

    @Overwrite @Nullable public static StatBase getBlockStats(Block block) { return BLOCK_STAT_MAP.get(block); }
    @Overwrite @Nullable public static StatBase getCraftStats(Item item) { return CRAFTS_STATS_MAP.get(item); }
    @Overwrite @Nullable public static StatBase getObjectUseStats(Item item) { return OBJECT_USE_STATS_MAP.get(item); }
    @Overwrite @Nullable public static StatBase getObjectBreakStats(Item item) { return OBJECT_BREAK_STATS_MAP.get(item); }
    @Overwrite @Nullable public static StatBase getObjectsPickedUpStats(Item item) { return OBJECTS_PICKED_UP_STATS_MAP.get(item); }
    @Overwrite @Nullable public static StatBase getDroppedObjectStats(Item item) { return OBJECTS_DROPPED_STATS_MAP.get(item); }

    @Overwrite
    public static void init() {
        initMiningStats();
        initStats();
        initItemDepleteStats();
        initCraftableStats();
        initPickedUpAndDroppedStats();
    }

    @Overwrite
    private static void initMiningStats() {
        for (Block block : Block.REGISTRY) {
            Item item = Item.getItemFromBlock(block);

            if (block.getEnableStats() && item != Items.AIR && getItemName(item) != null) {
                StatCrafting stat = new StatCrafting(
                        "stat.mineBlock.",
                        getItemName(item), new TextComponentTranslation("stat.mineBlock", new ItemStack(block).getTextComponent()),
                        item);

                MINE_BLOCK_STATS.add(stat);
                BLOCK_STAT_MAP.put(block, stat);
                stat.registerStat();
            }
        }
    }

    @Overwrite
    private static void initStats() {
        for (Item item : Item.REGISTRY) {
            if (item != null && getItemName(item) != null) {
                StatCrafting stat = new StatCrafting("stat.useItem.",
                                                     getItemName(item),
                                                     new TextComponentTranslation("stat.useItem", new ItemStack(item).getTextComponent()),
                                                     item);

                OBJECT_USE_STATS_MAP.put(item, stat);
                if (!(item instanceof ItemBlock)) USE_ITEM_STATS.add(stat);
                stat.registerStat();
            }
        }
    }

    @Overwrite
    private static void initCraftableStats() {
        Set<Item> craftableItems = Sets.newHashSet();

        for (IRecipe recipe : CraftingManager.REGISTRY) {
            ItemStack output = recipe.getRecipeOutput();
            if (!output.isEmpty()) craftableItems.add(recipe.getRecipeOutput().getItem());
        }

        for (ItemStack furnaceRecipeOutputs : FurnaceRecipes.instance().getSmeltingList().values()) {
            craftableItems.add(furnaceRecipeOutputs.getItem());
        }

        for (Item item : craftableItems) {
            if (item != null && getItemName(item) != null) {
                StatCrafting stat = new StatCrafting(
                        "stat.craftItem.",
                        getItemName(item),
                        new TextComponentTranslation("stat.craftItem", new ItemStack(item).getTextComponent()),
                        item);

                CRAFTS_STATS_MAP.put(item, stat);
                stat.registerStat();
            }
        }
    }

    @Overwrite
    private static void initItemDepleteStats() {
        for (Item item : Item.REGISTRY) {
            if (item != null && getItemName(item) != null && item.isDamageable()) {
                StatCrafting stat = new StatCrafting(
                        "stat.breakItem.",
                        getItemName(item),
                        new TextComponentTranslation("stat.breakItem", new ItemStack(item).getTextComponent()),
                        item);

                OBJECT_BREAK_STATS_MAP.put(item, stat);
                stat.registerStat();
            }
        }
    }

    private static void initPickedUpAndDroppedStats() {
        for (Item item : Item.REGISTRY)
            if (item != null && getItemName(item) != null) {
                StatCrafting pickupStat = new StatCrafting(
                        "stat.pickup.",
                        getItemName(item),
                        new TextComponentTranslation("stat.pickup", new ItemStack(item).getTextComponent()),
                        item);

                StatCrafting dropStat = new StatCrafting(
                        "stat.drop.",
                        getItemName(item),
                        new TextComponentTranslation("stat.drop", new ItemStack(item).getTextComponent()),
                        item);

                OBJECTS_PICKED_UP_STATS_MAP.put(item, pickupStat);
                OBJECTS_DROPPED_STATS_MAP.put(item, dropStat);
                pickupStat.registerStat();
                dropStat.registerStat();
            }
    }

    private static String getItemName(Item itemIn) {
        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(itemIn);
        return resourcelocation != null ? resourcelocation.toString().replace(':', '.') : null;
    }

    @Overwrite
    @Deprecated
    public static void reinit() { // Forge
        ID_TO_STAT_MAP.clear();
        BASIC_STATS.clear();
        USE_ITEM_STATS.clear();
        MINE_BLOCK_STATS.clear();

        HashSet<StatBase> knownStats = new HashSet<>();
        knownStats.addAll(BLOCK_STAT_MAP.values());
        knownStats.addAll(CRAFTS_STATS_MAP.values());
        knownStats.addAll(OBJECT_USE_STATS_MAP.values());
        knownStats.addAll(OBJECT_BREAK_STATS_MAP.values());
        knownStats.addAll(OBJECTS_PICKED_UP_STATS_MAP.values());
        knownStats.addAll(OBJECTS_DROPPED_STATS_MAP.values());

        List<StatBase> unknownStats = new ArrayList<>();
        for (StatBase stat : ALL_STATS) {
            if (!knownStats.contains(stat)) {
                unknownStats.add(stat);
            }
        }

        BLOCK_STAT_MAP.clear();
        CRAFTS_STATS_MAP.clear();
        OBJECT_USE_STATS_MAP.clear();
        OBJECT_BREAK_STATS_MAP.clear();
        OBJECTS_PICKED_UP_STATS_MAP.clear();
        OBJECTS_DROPPED_STATS_MAP.clear();
        ALL_STATS.clear();

        for (StatBase unknownStat : unknownStats) unknownStat.registerStat();

        init();
    }
}
