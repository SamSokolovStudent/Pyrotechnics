package net.soko.pyrotechnics.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.block.ModBlocks;

public class ModCreativeModeTab {
    public static CreativeModeTab PYROTECHNICS = null;

    public static void register(CreativeModeTabEvent.Register event) {
        PYROTECHNICS = event.registerCreativeModeTab(new ResourceLocation(PyroTechnics.MOD_ID, "pyrotechnics"), (builder) -> {
            builder.icon(() -> new ItemStack(Items.FIREWORK_ROCKET));
            builder.title(Component.translatable("itemGroup.pyrotechnics"));
        });
    }
    public static void buildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == PYROTECHNICS) {
            event.accept(ModItems.LARGE_FIRE_CHARGE.get());
            event.accept(ModItems.GUNPOWDER_FUSE.get());
            event.accept(ModItems.POWDER_FLASK.get());
            event.accept(ModItems.SALTPETER_DUST.get());
            event.accept(ModItems.SULFUR_DUST.get());
            event.accept(ModItems.ASH.get());
            event.accept(ModItems.RAW_PYRITE.get());
            event.accept(ModItems.PYRITE_ORE.get());
            event.accept(ModItems.CHARRED_LOG.get());
            event.accept(ModItems.CHARRED_GRASS_BLOCK.get());
            event.accept(ModItems.BURNT_GRASS.get());
            event.accept(ModItems.BURNT_PLANT.get());
        }
    }
}