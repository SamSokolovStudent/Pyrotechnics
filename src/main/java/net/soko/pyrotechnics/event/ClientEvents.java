package net.soko.pyrotechnics.event;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.capability.fieriness.ClientFierinessData;
import net.soko.pyrotechnics.entity.ModEntities;
import net.soko.pyrotechnics.math.SokoMath;

import java.util.ArrayList;

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


    public static void onFogAndSkyColors(ViewportEvent.ComputeFogColor event) {
        if (!event.getCamera().getEntity().level.getFluidState(event.getCamera().getBlockPosition()).isEmpty()) {
            return;
        }
        ChunkPos playerPos = new ChunkPos(event.getCamera().getBlockPosition());
        int[][] fierinessMatrix = new int[5][5];
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                fierinessMatrix[x + 2][z + 2] = ClientFierinessData.getChunkFieriness(new ChunkPos(playerPos.x + x, playerPos.z + z));
            }
        }
        ArrayList<Double> fierinessValues = new ArrayList<>();
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                fierinessValues.add((double) fierinessMatrix[x][z]);
            }
        }
        int[][] smoothedFierinessMatrix = SokoMath.convolveMatrixGaussian(fierinessMatrix, 1.4);
        int matrixSum = SokoMath.getMatrixSum(smoothedFierinessMatrix);
        float percentage = (float) (matrixSum / SokoMath.standardDeviation(fierinessValues));
        if (percentage > 1) {
            percentage = 1;
        }
        if (matrixSum > 0) {
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
        } else {
            event.setRed(event.getRed());
            event.setGreen(event.getGreen());
            event.setBlue(event.getBlue());
        }
    }
}
