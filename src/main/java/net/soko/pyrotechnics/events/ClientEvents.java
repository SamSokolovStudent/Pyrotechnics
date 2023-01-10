package net.soko.pyrotechnics.events;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.entity.ModEntities;

public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PYROTECHNICS_FIREWORK.get(), FireworkEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PYROTECHNICS_LARGE_FIRE_CHARGE.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GUNPOWDER_FUSE_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GUNPOWDER_ASH.get(), RenderType.cutout());
    }

}
