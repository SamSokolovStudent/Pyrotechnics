package net.soko.pyrotechnics.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PyroTechnics.MOD_ID);

    public static final RegistryObject<Item> FIREWORKS_BOX = ITEMS.register("fireworks_box",
            () -> new BlockItem(ModBlocks.FIREWORKS_BOX.get(), new Item.Properties()));
}
