package net.soko.pyrotechnics.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshItem extends Item {
    public AshItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.COMPOSTER)) {
            int i = state.getValue(ComposterBlock.LEVEL);
            float f = 0.85f;
            ItemStack itemstack = pContext.getItemInHand();
            itemstack.shrink(1);
            if (level.random.nextFloat() > 0.85f) {
            } else {
                int j = i + 1;
                BlockState blockstate = state.setValue(ComposterBlock.LEVEL, Integer.valueOf(j));
                level.setBlock(pos, blockstate, 3);
                if (j == 7) {
                    level.scheduleTick(pos, state.getBlock(), 20);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
