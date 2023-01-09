package net.soko.pyrotechnics.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;
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
}
