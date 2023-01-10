package net.soko.pyrotechnics.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PyroTechnics.MOD_ID);

    public static final RegistryObject<Item> FIREWORKS_BOX = ITEMS.register("fireworks_box",
            () -> new BlockItem(ModBlocks.FIREWORKS_BOX.get(), new Item.Properties()
                    .stacksTo(1)));

    public static final RegistryObject<Item> GUNPOWDER_FUSE = ITEMS.register("gunpowder_fuse",
            () -> new BlockItem(ModBlocks.GUNPOWDER_FUSE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> PYRITE_ORE = ITEMS.register("pyrite_ore",
            () -> new BlockItem(ModBlocks.PYRITE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> LARGE_FIRE_CHARGE = ITEMS.register("large_fire_charge",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)));

    public static final RegistryObject<Item> SALTPETER_DUST = ITEMS.register("saltpeter_dust",
            () -> new Item(new Item.Properties()
                    .fireResistant()));

    public static final RegistryObject<Item> SULFUR_DUST = ITEMS.register("sulfur_dust",
            () -> new Item(new Item.Properties()
                    .fireResistant()));

    public static final RegistryObject<Item> RAW_PYRITE = ITEMS.register("raw_pyrite",
            () -> new Item(new Item.Properties()));
}
