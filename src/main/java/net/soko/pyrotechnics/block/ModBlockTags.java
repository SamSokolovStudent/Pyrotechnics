package net.soko.pyrotechnics.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.soko.pyrotechnics.PyroTechnics;

public class ModBlockTags {
    public static TagKey<Block> EXPLODABLE = BlockTags.create(new ResourceLocation(PyroTechnics.MOD_ID, "explodable"));
}
