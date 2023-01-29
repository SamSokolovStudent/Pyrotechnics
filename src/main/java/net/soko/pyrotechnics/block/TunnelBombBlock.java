package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TunnelBombBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final VoxelShape SHAPE_NORTH = Block.box(5, 4, 13, 11, 12, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5, 4, 0, 11, 12, 3);
    private static final VoxelShape SHAPE_WEST = Block.box(13, 4, 5, 16, 12, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 5, 3, 12, 11);
    private static final VoxelShape CEILING_X = Block.box(5, 0, 5, 11, 3, 11);
    private static final VoxelShape CEILING_Z = Block.box(5, 0, 5, 11, 3, 11);
    private static final VoxelShape FLOOR_X = Block.box(5, 13, 5, 11, 16, 11);
    private static final VoxelShape FLOOR_Z = Block.box(5, 13, 5, 11, 16, 11);


    public TunnelBombBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        Direction direction = blockState.getValue(FACING);
        switch (blockState.getValue(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return FLOOR_X;
                } else {
                    return FLOOR_Z;
                }
            case WALL:
                switch(direction) {
                    case EAST:
                        return SHAPE_EAST;
                    case WEST:
                        return SHAPE_WEST;
                    case SOUTH:
                        return SHAPE_SOUTH;
                    case NORTH:
                    default:
                        return SHAPE_NORTH;
                }
            case CEILING:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_X;
                } else {
                    return CEILING_Z;
                }
            default:
                throw new IncompatibleClassChangeError("Invalid face: " + blockState.getValue(FACE));
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, FACE);
    }
}
