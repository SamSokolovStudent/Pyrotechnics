package net.soko.pyrotechnics.block.entity;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.soko.pyrotechnics.block.CharredGrassBlock;
import net.soko.pyrotechnics.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class CharredGrassBlockEntity extends BlockEntity {
    private static final int MAX_ITERATIONS = 5;
    private static final int POINTS_PER_ITERATION = 4;
    private final List<BlockPos> positions;
    private int iteration;

    public CharredGrassBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHARRED_GRASS_BLOCK_ENTITY.get(), pos, state);
        positions = Lists.newArrayList(pos);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CharredGrassBlockEntity pBlockEntity) {
        if (pLevel.getBlockState(pPos).getValue(CharredGrassBlock.IS_SOURCE)) {
            if (pBlockEntity.iteration >= MAX_ITERATIONS) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(CharredGrassBlock.IS_SOURCE, false));
                pLevel.removeBlockEntity(pPos);
                return;
            }
            ArrayList<BlockPos> tempPositions = Lists.newArrayList();
            for (BlockPos position : pBlockEntity.positions) {
                AABB aabb = new AABB(position).inflate(1);
                List<BlockPos> possiblePositions = new ArrayList<>(BlockPos.betweenClosedStream(aabb).filter(pos -> !pos.equals(position) && pLevel.getBlockState(pos).is(Blocks.GRASS_BLOCK)).toList());
                for (int i = 0; i < CharredGrassBlockEntity.POINTS_PER_ITERATION; i++) {
                    if (possiblePositions.isEmpty()) {
                        break;
                    }
                    if (pLevel.random.nextInt(5) == 0) {
                        BlockPos possiblePosition = possiblePositions.remove(pLevel.random.nextInt(possiblePositions.size()));
                        pLevel.setBlockAndUpdate(possiblePosition, ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true));
                        tempPositions.add(possiblePosition);
                    }
                }
            }
            pBlockEntity.positions.clear();
            pBlockEntity.positions.addAll(tempPositions);
            pBlockEntity.iteration++;
        }
    }
}

