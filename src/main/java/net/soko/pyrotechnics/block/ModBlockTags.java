package net.soko.pyrotechnics.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.soko.pyrotechnics.PyroTechnics;

public class ModBlockTags {
    public static TagKey<Block> EXPLODABLE = BlockTags.create(new ResourceLocation(PyroTechnics.MOD_ID, "explodable"));
    public static TagKey<Block> FUSE_CONNECTABLE = BlockTags.create(new ResourceLocation(PyroTechnics.MOD_ID, "fuse_connectable"));
    public static TagKey<Block> CHARRABLE_GRASS = BlockTags.create(new ResourceLocation(PyroTechnics.MOD_ID, "charrable_grass"));
}
