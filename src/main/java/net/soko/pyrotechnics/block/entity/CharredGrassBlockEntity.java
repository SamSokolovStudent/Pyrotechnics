package net.soko.pyrotechnics.block.entity;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.soko.pyrotechnics.block.CharredGrassBlock;
import net.soko.pyrotechnics.block.ModBlockTags;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.capability.fieriness.FierinessManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CharredGrassBlockEntity extends BlockEntity {
    private static final int MAX_ITERATIONS = 5;
    private static final int POINTS_PER_ITERATION = 2;
    private final HashSet<BlockPos> positions = new HashSet<>();
    private int iteration;

    public CharredGrassBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHARRED_GRASS_BLOCK_ENTITY.get(), pos, state);
        //positions = Lists.newArrayList(pos.immutable());
        positions.add(pos.immutable());
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CharredGrassBlockEntity pBlockEntity) {
        ArrayList<BlockPos> tempPositions = Lists.newArrayList();
        for (BlockPos position : pBlockEntity.positions) {
            // for loop to get possible positions instead of streams as betweenClosedStream each pos is the same object.
            List<BlockPos> possiblePositions = new ArrayList<>();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        mutableBlockPos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);
                        if (!pLevel.getBlockState(mutableBlockPos).is(ModBlockTags.CHARRABLE_GRASS)) continue;
                        possiblePositions.add(mutableBlockPos.immutable());
                    }
                }
            }

            for (int i = 0; i < CharredGrassBlockEntity.POINTS_PER_ITERATION; i++) {
                if (possiblePositions.isEmpty()) {
                    break;
                }
                if (pLevel.random.nextInt(5) == 0) {
                    BlockPos possiblePosition = possiblePositions.remove(pLevel.random.nextInt(possiblePositions.size()));
                    pLevel.setBlockAndUpdate(possiblePosition, ModBlocks.CHARRED_GRASS_BLOCK.get().defaultBlockState().setValue(CharredGrassBlock.SMOLDERING, true));
                    tempPositions.add(possiblePosition);
                    FierinessManager.get(pLevel).increaseFieriness(possiblePosition, 1);
                }
            }
        }
        pBlockEntity.positions.addAll(tempPositions);
        pBlockEntity.iteration++;
        if (pBlockEntity.iteration >= MAX_ITERATIONS) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(CharredGrassBlock.IS_SOURCE, false));
            pLevel.removeBlockEntity(pPos);
        }

    }
}

