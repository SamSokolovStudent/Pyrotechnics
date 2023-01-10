package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GunpowderAshBlock extends Block {
    public static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);

    public GunpowderAshBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
