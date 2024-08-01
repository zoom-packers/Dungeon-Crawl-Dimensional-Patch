package com.pandaismyname1.dungeoncrawldimensionalpatch;

import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import static xiroc.dungeoncrawl.dungeon.treasure.Loot.*;

public class Utils {
    public static String getDimensionName(LevelAccessor levelAccessor) {
        if (levelAccessor instanceof WorldGenRegion region) {
            var dimension = region.getLevel().dimension().location().toString();
            var dimensionName = dimension.substring(dimension.indexOf(":") + 1);
            return dimensionName;
        }
        return "UNKNOWN";
    }


    public static ResourceLocation getLootTable(LevelAccessor world, int lootLevel, RandomSource rand) {
        var dimensionName = Utils.getDimensionName(world);
        if (Dimension.DIMENSIONS.containsKey(dimensionName)) {
            var dimension = Dimension.DIMENSIONS.get(dimensionName);
            if (lootLevel < dimension.chests.size()) {
                return dimension.chests.get(lootLevel);
            }
        }
        return switch (lootLevel) {
            case 0 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.JUNGLE_TEMPLE : CHEST_STAGE_1;
            case 1 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_2;
            case 2 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_3;
            case 3 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_4;
            case 4 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_5;
            default -> Loot.CHEST_STAGE_5;
        };

    }
}
