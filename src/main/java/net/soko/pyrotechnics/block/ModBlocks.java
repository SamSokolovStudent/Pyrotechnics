package net.soko.pyrotechnics.block;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.block.entity.CharredGrassBlockEntity;
import net.soko.pyrotechnics.block.entity.FireworksBoxBlockEntity;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PyroTechnics.MOD_ID);

    public static final RegistryObject<Block> FIREWORKS_BOX = BLOCKS.register("fireworks_box",
            () -> new FireworksBoxBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.0F, 1.0F)
                    .sound(SoundType.BAMBOO_WOOD)
                    .noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> true)
                    .instabreak()));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PyroTechnics.MOD_ID);

    public static final RegistryObject<BlockEntityType<FireworksBoxBlockEntity>> FIREWORKS_BOX_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("fireworks_box",
                    () -> BlockEntityType.Builder.of(FireworksBoxBlockEntity::new, FIREWORKS_BOX.get()).build(null));

    public static final RegistryObject<Block> GUNPOWDER_FUSE_BLOCK = BLOCKS.register("gunpowder_fuse",
            () -> new GunpowderFuseBlock(Block.Properties.of(Material.SAND)
                    .strength(0.0F, 0.0F)
                    .sound(SoundType.SAND)
                    .noOcclusion()
                    .instabreak()
                    .noCollission()));

    public static final RegistryObject<Block> GUNPOWDER_ASH = BLOCKS.register("gunpowder_ash",
            () -> new GunpowderAshBlock(Block.Properties.of(Material.SAND)
                    .strength(0.0F, 0.0F)
                    .sound(SoundType.SAND)
                    .noOcclusion()
                    .instabreak()
                    .noCollission()));

    public static final RegistryObject<Block> PYRITE_ORE = BLOCKS.register("pyrite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2, 2.8F)
                    .sound(SoundType.NETHER_GOLD_ORE)));

    public static final RegistryObject<Block> CHARRED_LOG = BLOCKS.register("charred_log",
            () -> new CharredLogBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 3)
                    .sound(SoundType.BASALT)));

    public static final RegistryObject<Block> CHARRED_GRASS_BLOCK = BLOCKS.register("charred_grass_block",
            () -> new CharredGrassBlock(BlockBehaviour.Properties.of(Material.DIRT)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .sound(SoundType.ROOTED_DIRT)));

    public static final RegistryObject<BlockEntityType<CharredGrassBlockEntity>> CHARRED_GRASS_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("charred_grass_block",
                    () -> BlockEntityType.Builder.of(CharredGrassBlockEntity::new, CHARRED_GRASS_BLOCK.get()).build(null));

    public static final RegistryObject<Block> BURNT_GRASS = BLOCKS.register("burnt_grass",
            () -> new BurntFoliageBlock((BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ))));

    public static final RegistryObject<Block> BURNT_PLANT = BLOCKS.register("burnt_plant",
            () -> new BurntFoliageBlock((BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ))));

}
