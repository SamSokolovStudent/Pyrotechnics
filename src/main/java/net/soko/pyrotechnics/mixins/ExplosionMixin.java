package net.soko.pyrotechnics.mixins;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.soko.pyrotechnics.data.loot.ModLootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private Explosion.BlockInteraction blockInteraction;

    @Inject(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void pyrotechnics$onLootContextCreation(boolean pSpawnParticles, CallbackInfo ci, boolean flag, ObjectArrayList objectarraylist, boolean flag1, ObjectListIterator var5, BlockPos blockpos, BlockState blockstate, Block block, BlockPos blockpos1, Level $$9, ServerLevel serverlevel, BlockEntity blockentity, LootContext.Builder lootcontext$builder) {
        lootcontext$builder.withParameter(ModLootContextParams.EXPLOSION_BLOCK_INTERACTION, this.blockInteraction);
    }
}
