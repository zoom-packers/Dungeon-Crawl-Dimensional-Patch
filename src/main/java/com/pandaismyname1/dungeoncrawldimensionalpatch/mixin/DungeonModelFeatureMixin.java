package com.pandaismyname1.dungeoncrawldimensionalpatch.mixin;

import com.pandaismyname1.dungeoncrawldimensionalpatch.Utils;
import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

@Mixin(DungeonModelFeature.class)
public class DungeonModelFeatureMixin {

    @Inject(method = "placeChest", at = @At(value = "INVOKE", target = "Lxiroc/dungeoncrawl/dungeon/treasure/Loot;setLoot(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;Lnet/minecraft/resources/ResourceLocation;Lxiroc/dungeoncrawl/theme/Theme;Lxiroc/dungeoncrawl/theme/SecondaryTheme;Lnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER), remap = false)
    private static void placeChest(LevelAccessor world, BlockPos pos, BlockState chest, Theme theme, SecondaryTheme secondaryTheme, int lootLevel, RandomSource rand, CallbackInfo ci) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            Loot.setLoot(world, pos, randomizableContainerBlockEntity, Utils.getLootTable(world, lootLevel, rand), theme, secondaryTheme, rand);
        }
    }

}
