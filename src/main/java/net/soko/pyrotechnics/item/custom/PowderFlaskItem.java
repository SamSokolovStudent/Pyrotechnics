package net.soko.pyrotechnics.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.soko.pyrotechnics.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.List;

public class PowderFlaskItem extends Item {

    private static final int BAR_COLOR = Mth.color(90, 90, 90);

    public PowderFlaskItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Direction face = context.getClickedFace();
        int gunpowder = getGunpowder(itemstack);
        if (gunpowder > 0) {
            if (!state.isAir()) {
                if (state.getMaterial().isReplaceable() && state.getFluidState().isEmpty()) {
                    BlockState belowState = level.getBlockState(pos.below());
                    if (belowState.isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
                        level.setBlockAndUpdate(pos, ModBlocks.GUNPOWDER_FUSE_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, context.getHand(), itemstack, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false))));
                        if (!player.isCreative()) {
                            addGunpowder(itemstack, -1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                } else if (level.getBlockState(pos.above()).isAir() && state.isFaceSturdy(level, pos, face)) {
                    if (face == Direction.UP) {
                        level.setBlockAndUpdate(pos.above(), ModBlocks.GUNPOWDER_FUSE_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, context.getHand(), itemstack, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false))));
                        if (!player.isCreative()) {
                            addGunpowder(itemstack, -1);
                        }
                        return InteractionResult.SUCCESS;
                    } else {
                        level.setBlockAndUpdate(pos.relative(face), ModBlocks.GUNPOWDER_FUSE_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, context.getHand(), itemstack, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false))));
                        if (!player.isCreative()) {
                            addGunpowder(itemstack, -1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int size = getGunpowder(pStack);
        return (int) (13.0 * size / 256.0);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return BAR_COLOR;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level
            pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.pyrotechnics.powder_flask.lore").withStyle(ChatFormatting.GRAY).append(Component.literal(("" + getGunpowder(pStack))).withStyle(ChatFormatting.AQUA)));
    }

    public static int getGunpowder(ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag().getInt("Gunpowder");
        }
        return 0;
    }

    public static void addGunpowder(ItemStack stack, int amount) {
        stack.getOrCreateTag().putInt("Gunpowder", getGunpowder(stack) + amount);
    }

}
