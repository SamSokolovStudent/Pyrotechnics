package net.soko.pyrotechnics.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.capability.fieriness.ClientFierinessData;
import net.soko.pyrotechnics.capability.fieriness.FierinessClientManager;
import net.soko.pyrotechnics.entity.ModEntities;

public class ClientEvents {
    private static final int FOG_RED = 0xEF;
    private static final int FOG_GREEN = 0x5a;
    private static final int FOG_BLUE = 0x00;

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


    public static void calculateFieriness(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ClientFierinessData.update();
    }


    public static void onFogAndSkyColors(ViewportEvent.ComputeFogColor event) {
        if (!event.getCamera().getEntity().level.getFluidState(event.getCamera().getBlockPosition()).isEmpty()) {
            return;
        }

        float percentage = ClientFierinessData.getFierinessPercentage();

        if (percentage > 0) {
            float linearRed = (float) Mth.lerp(percentage, event.getRed(), FOG_RED / 255.0);
            float linearGreen = (float) Mth.lerp(percentage, event.getGreen(), FOG_GREEN / 255.0);
            float linearBlue = (float) Mth.lerp(percentage, event.getBlue(), FOG_BLUE / 255.0);
            if (event.getCamera().getEntity() instanceof LivingEntity entity) {
                if (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS)) {

                    linearRed = (float) Mth.lerp(percentage, event.getRed(), 0.0);
                    linearGreen = (float) Mth.lerp(percentage, event.getGreen(), 0.0);
                    linearBlue = (float) Mth.lerp(percentage, event.getBlue(), 0.0);

                }
            }

            event.setRed(linearRed);
            event.setGreen(linearGreen);
            event.setBlue(linearBlue);

        }

    }

    public static void onRenderFog(ViewportEvent.RenderFog event) {
        FierinessClientManager manager = FierinessClientManager.getFierinessClientManager();
        if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            float renderDistance = event.getRenderer().getRenderDistance();
            float fogMultiplier = 1.0f;
            if (Minecraft.getInstance().level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
                fogMultiplier = (float) Mth.lerp(manager.getUndergroundFactor((float) event.getPartialTick()), 1, 0.0f);
                float fieriness = ClientFierinessData.updateWithPartial((float) event.getPartialTick());
                fogMultiplier = Mth.lerp(fogMultiplier, fieriness, 0.0f);
            }

            RenderSystem.setShaderFogStart(renderDistance * ClientFierinessData.updateWithPartial((float) event.getPartialTick()));
            RenderSystem.setShaderFogEnd(renderDistance * ClientFierinessData.updateWithPartial((float) event.getPartialTick()) * fogMultiplier);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }


}
