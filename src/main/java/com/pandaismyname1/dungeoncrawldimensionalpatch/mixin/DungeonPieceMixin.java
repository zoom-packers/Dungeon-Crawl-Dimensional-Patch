package com.pandaismyname1.dungeoncrawldimensionalpatch.mixin;

import com.pandaismyname1.dungeoncrawldimensionalpatch.Utils;
import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;

import static xiroc.dungeoncrawl.dungeon.treasure.Loot.*;

@Mixin(DungeonPiece.class)
public class DungeonPieceMixin {

    @Shadow
    public DungeonModel model;

    @Shadow
    public int stage;


    @Inject(method = "placeBlock", at = @At(value = "INVOKE", target = "Lxiroc/dungeoncrawl/dungeon/treasure/Loot;setLoot(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;Lnet/minecraft/resources/ResourceLocation;Lxiroc/dungeoncrawl/theme/Theme;Lxiroc/dungeoncrawl/theme/SecondaryTheme;Lnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER), remap = false, cancellable = true)
    public void placeBlock(LevelAccessor world, BlockState state, BlockPos position, DungeonModelBlock block, Rotation rotation, RandomSource random, PlacementConfiguration configuration,
                           Theme theme, SecondaryTheme secondaryTheme, List<BlockPos> fancyPillars, int lootLevel, boolean fillAir, boolean expandDownwards, CallbackInfo ci) {

        BlockEntity tile = world.getBlockEntity(position);
        if (tile instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            if (!model.hasLootTable()) {
                Loot.setLoot(world, position, randomizableContainerBlockEntity, Utils.getLootTable(world, stage, random), theme, secondaryTheme, random);
            }
        }
    }
}
