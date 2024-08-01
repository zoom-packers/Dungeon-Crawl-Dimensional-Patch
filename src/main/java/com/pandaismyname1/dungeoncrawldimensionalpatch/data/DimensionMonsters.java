package com.pandaismyname1.dungeoncrawldimensionalpatch.data;

import xiroc.dungeoncrawl.dungeon.monster.WeightedRandomEntity;

public class DimensionMonsters {
    public WeightedRandomEntity[] COMMON, RARE;

    public DimensionMonsters(int stages) {
        COMMON = new WeightedRandomEntity[stages];
        RARE = new WeightedRandomEntity[stages];
    }
}
