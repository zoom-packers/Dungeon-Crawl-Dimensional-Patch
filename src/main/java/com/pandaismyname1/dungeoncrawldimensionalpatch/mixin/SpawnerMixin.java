package com.pandaismyname1.dungeoncrawldimensionalpatch.mixin;

import com.pandaismyname1.dungeoncrawldimensionalpatch.Utils;
import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import com.pandaismyname1.dungeoncrawldimensionalpatch.data.DimensionMonsters;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xiroc.dungeoncrawl.dungeon.block.Spawner;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

@Mixin(Spawner.class)
public class SpawnerMixin {

    @Inject(method = "place", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/BaseSpawner;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)V",
            shift = At.Shift.AFTER)
    )
    private void changeSpawner(LevelAccessor world, BlockState state, BlockPos pos, RandomSource rand, Theme theme, SecondaryTheme secondaryTheme, int stage, CallbackInfo ci) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SpawnerBlockEntity spawner) {
            var dimensionName = Utils.getDimensionName(world);
            if (Dimension.DIMENSIONS.containsKey(dimensionName)) {
                var dimension = Dimension.DIMENSIONS.get(dimensionName);
                var monsters = dimension.monsters;
                EntityType<?> type = dungeonCrawlDimensionalPatch$randomMonster(monsters, rand, stage);
                spawner.getSpawner().setEntityId(type, null, rand, pos);
            }
        }
    }

    @Unique
    private static EntityType<?> dungeonCrawlDimensionalPatch$randomMonster(DimensionMonsters dimensionMonsters, RandomSource rand, int stage) {
        if (stage > 4)
            stage = 4;
        if (rand.nextFloat() < 0.1) {
            EntityType<?> monster = dimensionMonsters.RARE[stage].roll(rand);
            if (monster != null) {
                return monster;
            } else {
                return dimensionMonsters.COMMON[stage].roll(rand);
            }
        }
        return dimensionMonsters.COMMON[stage].roll(rand);
    }

}
