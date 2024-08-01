package com.pandaismyname1.dungeoncrawldimensionalpatch.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.WeightedRandomEntity;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dimension {

    public static HashMap<String, Dimension> DIMENSIONS = new HashMap<>();
    public static ResourceManager RESOURCE_MANAGER;

    public String name = "";
    public int stages;

    public DimensionMonsters monsters;
    public List<WeightedRandom<Item>> treasures = new ArrayList<>();
    public List<ResourceLocation> chests = new ArrayList<>();

    public Dimension(String name) {
        this.name = name;
        extractLevels(RESOURCE_MANAGER);
    }

    private void extractLevels(ResourceManager resourceManager) {
        try {
        var levelsFile = DungeonCrawl.locate("monster/entities/" + name + "/levels.json");
        Resource resource = resourceManager.getResource(levelsFile).orElseThrow(() -> new DatapackLoadException("Missing file: " + levelsFile));
            stages = JsonParser.parseReader(new JsonReader(new InputStreamReader(resource.open()))).getAsInt();
            monsters = new DimensionMonsters(stages);
            treasures = new ArrayList<>(stages);
            chests = new ArrayList<>(stages);
            for (int i = 0; i < stages; i++) {
                extractMobsForStageX(resourceManager, name, monsters, i);
                extractTreasureForStageX(resourceManager, i);
                extractChestForStageX(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch ( ResourceLocationException e) {
            e.printStackTrace();
        } catch (DatapackLoadException e) {
            e.printStackTrace();
        }
    }

    private void extractChestForStageX(int stage) {
        var chestFile = DungeonCrawl.locate("chests/" + name + "/stage_" + (stage + 1));
        chests.add(chestFile);
    }

    private void extractTreasureForStageX(ResourceManager resourceManager, int stage) throws IOException {
        var treasureFile = DungeonCrawl.locate("treasure/" + name + "/stage_" + (stage + 1) + ".json");
        JsonArray array = JsonParser.parseReader(new JsonReader(new InputStreamReader(resourceManager.getResource(treasureFile).orElseThrow().open()))).getAsJsonArray();
        treasures.add(WeightedRandom.ITEM.fromJson(array));
    }

    private void extractMobsForStageX(ResourceManager resourceManager, String dimensionName, DimensionMonsters dimensionMonsters, int stage) {
        var entityFile = DungeonCrawl.locate("monster/entities/" + dimensionName + "/stage_" + (stage + 1) + ".json");
        try {
            assignEntityData(resourceManager, entityFile, dimensionMonsters, stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assignEntityData(ResourceManager resourceManager, ResourceLocation file, DimensionMonsters dimensionData, int stage) throws IOException {

        DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
        Resource resource = resourceManager.getResource(file).orElseThrow(() -> new DatapackLoadException("Missing file: " + file));
        JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(resource.open()))).getAsJsonObject();

        if (object.has("common")) {
            dimensionData.COMMON[stage] = WeightedRandomEntity.fromJson(object.getAsJsonArray("common"));
        } else {
            DungeonCrawl.LOGGER.warn("Missing entry 'common' in {}", file.toString());
            dimensionData.COMMON[stage] = WeightedRandomEntity.EMPTY;
        }

        if (object.has("rare")) {
            dimensionData.RARE[stage] = WeightedRandomEntity.fromJson(object.getAsJsonArray("rare"));
        } else {
            DungeonCrawl.LOGGER.warn("Missing entry 'rare' in {}", file.toString());
            dimensionData.RARE[stage] = WeightedRandomEntity.EMPTY;
        }
    }

}
