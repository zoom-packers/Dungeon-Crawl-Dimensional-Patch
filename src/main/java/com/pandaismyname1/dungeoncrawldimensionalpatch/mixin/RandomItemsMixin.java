package com.pandaismyname1.dungeoncrawldimensionalpatch.mixin;

import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomItem;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.ArrayList;

@Mixin(RandomItem.class)
public class RandomItemsMixin {

    @Shadow
    public int lootLevel;

    @Inject(method = "m_7372_", at = @At("HEAD"), cancellable = true)
    public void run(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (context.hasParam(LootContextParams.ORIGIN)) {
            var result = dungeonCrawlDimensionalPatch$generate(context.getLevel(), context.getRandom(), lootLevel);
            if (result != null) {
                cir.setReturnValue(result);
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }


    @Unique
    private static ItemStack dungeonCrawlDimensionalPatch$generate(ServerLevel serverLevel, RandomSource rand, int lootLevel) {
        ItemStack stack = new ItemStack(dungeonCrawlDimensionalPatch$itemProvider(serverLevel, lootLevel).roll(rand));
        if (rand.nextFloat() < 0.5F + 0.1F * lootLevel) {
            return EnchantmentHelper.enchantItem(rand, stack, 10 + 3 * lootLevel, lootLevel > 2);
        }
        return stack;
    }


    @Unique
    private static WeightedRandom<Item> dungeonCrawlDimensionalPatch$itemProvider(ServerLevel serverLevel, int stage) {
        var fullDimensionName = serverLevel.dimension().location().toString();
        var dimensionName = fullDimensionName.substring(fullDimensionName.indexOf(":") + 1);
        if (Dimension.DIMENSIONS.containsKey(dimensionName)) {
            var dimension = Dimension.DIMENSIONS.get(dimensionName);
            if (stage >= dimension.stages) {
                stage = dimension.stages - 1;
            }
            return dimension.treasures.get(stage);
        }
        var item =new Tuple<Item, Integer>(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "coal")), 1);
        var array = new ArrayList<Tuple<Item, Integer>>();
        array.add(item);
        return new WeightedRandom<Item>(array);
    }
}
