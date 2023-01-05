package net.soko.pyrotechnics.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.soko.pyrotechnics.PyroTechnics;

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
            event.accept(ModItems.FIREWORKS_BOX.get());
        }
    }
}