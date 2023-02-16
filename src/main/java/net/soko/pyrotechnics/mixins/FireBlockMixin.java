package net.soko.pyrotechnics.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.soko.pyrotechnics.block.CharredGrassBlock;
import net.soko.pyrotechnics.block.CharredLogBlock;
import net.soko.pyrotechnics.block.ModBlockTags;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.capability.fieriness.FierinessManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "tryCatchFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    public void pyrotechnics$removeBlock(@NotNull Level level, BlockPos pos, int humidity, RandomSource randomSource, int age, Direction face, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.LOGS_THAT_BURN)) {
            pyrotechnics$charBlock(level, pos, state);
        } else if (state.is(BlockTags.LEAVES)) {
            if (randomSource.nextFloat() < 0.25) {
                pyrotechnics$setNeighbourFire(level, pos);
                FierinessManager.get(level).increaseFieriness(pos, 4);
                if (level.getBlockState(pos.below()).is(ModBlockTags.CHARRABLE_GRASS)) {
                    level.setBlockAndUpdate(pos.below(), ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true).setValue(CharredGrassBlock.IS_SOURCE, true).setValue(CharredGrassBlock.LIT, true));
                }
            }
            level.removeBlock(pos, false);
        } else if (state.is(BlockTags.REPLACEABLE_PLANTS)) {
            pyrotechnics$charGrass(level, pos);
            pyrotechnics$setNeighbourFire(level, pos);
        } else {
            level.removeBlock(pos, false);
        }
        state.onCaughtFire(level, pos, face, null);
        ci.cancel();
    }


    @Inject(method = "tryCatchFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    public void pyrotechnics$setBlock(@NotNull Level level, BlockPos pos, int humidity, RandomSource randomSource, int age, Direction face, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.LOGS_THAT_BURN)) {
            pyrotechnics$charBlock(level, pos, state);
        } else if (state.is(BlockTags.LEAVES)) {
            if (randomSource.nextFloat() < 0.25) {
                pyrotechnics$setNeighbourFire(level, pos);
                FierinessManager.get(level).increaseFieriness(pos, 4);
                if (level.getBlockState(pos.below()).is(ModBlockTags.CHARRABLE_GRASS)) {
                    level.setBlockAndUpdate(pos.below(), ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true).setValue(CharredGrassBlock.IS_SOURCE, true).setValue(CharredGrassBlock.LIT, true));
                }
            }
            level.removeBlock(pos, false);
        } else if (state.is(BlockTags.REPLACEABLE_PLANTS)) {
            pyrotechnics$charGrass(level, pos);
            pyrotechnics$setNeighbourFire(level, pos);
        } else {
            level.setBlock(pos, state, 3);
        }
        state.onCaughtFire(level, pos, face, null);
        ci.cancel();
    }

    @Unique
    private static void pyrotechnics$charBlock(Level level, BlockPos pos, BlockState state) {
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            Direction.Axis rotationState = state.getValue(RotatedPillarBlock.AXIS);
            level.setBlockAndUpdate(pos, ModBlocks.CHARRED_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, rotationState).setValue(CharredLogBlock.SMOLDERING, true).setValue(CharredLogBlock.LIT, true));
        } else {
            level.setBlockAndUpdate(pos, ModBlocks.CHARRED_LOG.get().defaultBlockState().setValue(CharredLogBlock.SMOLDERING, true).setValue(CharredLogBlock.LIT, true));
        }
        FierinessManager.get(level).increaseFieriness(pos, 50);
        pyrotechnics$setNeighbourFire(level, pos);
    }

    @Unique
    private static void pyrotechnics$setNeighbourFire(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = mutablePos.setWithOffset(pos, direction);
            BlockState neighbourState = level.getBlockState(neighbourPos);
            if (neighbourState.is(BlockTags.LOGS_THAT_BURN) || neighbourState.is(BlockTags.LEAVES)) {
                for (Direction neighbourDirection : Direction.values()) {
                    BlockPos neighbourNeighbourPos = mutablePos.setWithOffset(neighbourPos, neighbourDirection);
                    if (BaseFireBlock.canBePlacedAt(level, neighbourNeighbourPos, neighbourDirection.getOpposite())) {
                        level.setBlockAndUpdate(neighbourNeighbourPos, BaseFireBlock.getState(level, neighbourNeighbourPos));
                    }
                }
            }
        }
    }

    @Unique
    private static void pyrotechnics$charGrass(Level level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.GRASS)) {
            level.setBlockAndUpdate(pos, ModBlocks.BURNT_GRASS.get().defaultBlockState());
            if (level.getBlockState(pos.below()).is(ModBlockTags.CHARRABLE_GRASS)) {
                level.setBlockAndUpdate(pos.below(), ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true).setValue(CharredGrassBlock.IS_SOURCE, true).setValue(CharredGrassBlock.LIT, true));
            }
        } else {
            level.setBlock(pos, ModBlocks.BURNT_PLANT.get().defaultBlockState(), 3);
            if (level.getBlockState(pos.below()).is(ModBlockTags.CHARRABLE_GRASS)) {
                level.setBlockAndUpdate(pos.below(), ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true).setValue(CharredGrassBlock.IS_SOURCE, true).setValue(CharredGrassBlock.LIT, true));
            }
        }
        FierinessManager.get(level).increaseFieriness(pos, 12);

    }
}

