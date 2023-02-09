package net.soko.pyrotechnics.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.data.loot.IsExplosionCondition;
import net.soko.pyrotechnics.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PyrotechnicsBlockLootProvider extends BlockLootSubProvider {
    private final Set<Block> blocks = new HashSet<>();

    public PyrotechnicsBlockLootProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void add(@NotNull Block pBlock, LootTable.@NotNull Builder pBuilder) {
        super.add(pBlock, pBuilder);
        blocks.add(pBlock);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return blocks;
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.TUNNEL_BOMB.get());
        otherWhenSilkTouch(ModBlocks.CHARRED_GRASS_BLOCK.get(), Blocks.DIRT);
        this.add(ModBlocks.BURNT_PLANT.get(), createShearsOnlyDrop(ModBlocks.BURNT_PLANT.get()));
        this.add(ModBlocks.BURNT_GRASS.get(), createShearsOnlyDrop(ModBlocks.BURNT_GRASS.get()));
        dropSilktouchExplosionBlock(ModBlocks.CHARRED_LOG.get(), Items.CHARCOAL, ModItems.ASH.get(), ModItems.CHARRED_LOG.get());
        dropSilktouchExplosionBlock(ModBlocks.PYRITE_ORE.get(), ModItems.RAW_PYRITE.get(), ModItems.SULFUR_DUST.get(), ModItems.PYRITE_ORE.get());
    }

    private void dropSilktouchExplosionBlock(Block block, ItemLike normalDrop, ItemLike explosionDrop, ItemLike silkTouchDrop) {
        add(block, LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(explosionDrop)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                        .when(IsExplosionCondition.condition())
                                )
                                .add(LootItem.lootTableItem(silkTouchDrop)
                                        .when(HAS_SILK_TOUCH)
                                        .otherwise(LootItem.lootTableItem(normalDrop)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                                                .when(IsExplosionCondition.condition().invert())
                                        )
                                )
                )
        );
    }
}
