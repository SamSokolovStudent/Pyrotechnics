package net.soko.pyrotechnics.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PyroTechnics.MOD_ID);

    public static final RegistryObject<Block> FIREWORKS_BOX = BLOCKS.register("fireworks_box",
            () -> new FireworksBoxBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD)));
}
