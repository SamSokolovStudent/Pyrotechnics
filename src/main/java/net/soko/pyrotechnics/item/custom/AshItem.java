package net.soko.pyrotechnics.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.soko.pyrotechnics.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class AshItem extends Item {
    public AshItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.COMPOSTER)) {
            int i = state.getValue(ComposterBlock.LEVEL);
            float f = 0.7f;
            ItemStack itemstack = pContext.getItemInHand();
            itemstack.shrink(1);
            if (level.random.nextFloat() > f) {
            } else {
                int j = i + 1;
                BlockState blockState = state.setValue(ComposterBlock.LEVEL, j);
                level.setBlockAndUpdate(pos, blockState);
                if (j == 7) {
                    level.scheduleTick(pos, state.getBlock(), 20);
                }
            }
            return InteractionResult.SUCCESS;
        } else if (state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) > 0) {
            ItemStack itemstack = pContext.getItemInHand();
            // Get count of ash in stack, limit to 16 per cauldron water level
            int itemStackCount = Math.min(itemstack.getCount(), 16);
            itemstack.shrink(itemStackCount);
            // randomly remove 1-4 items from itemStackCount
            itemStackCount = itemStackCount - level.random.nextInt(6);
            if (state.getValue(LayeredCauldronBlock.LEVEL) > 1) {
                level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL) - 1));
            } else {
                level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            }
            double d0 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
            double d1 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
            double d2 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
            for (int i = 0; i < itemStackCount; i++) {
                ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, new ItemStack(ModItems.SALTPETER_DUST.get()));
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
}
