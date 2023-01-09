package net.soko.pyrotechnics.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.soko.pyrotechnics.block.FireworksBoxBlock;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.entity.PyrotechnicsFirework;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireworksBoxBlockEntity extends BlockEntity {

    private int ticks = 0;

    private final List<ItemStack> fireworks = new ArrayList<>();

    public FireworksBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.FIREWORKS_BOX_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void activate(ItemStack fireworkRocket, double loopScale, float radianSpeed) {
        activate(this.level, this.worldPosition, fireworkRocket, loopScale, radianSpeed);
    }

    public static void activate(Level level, BlockPos pos, ItemStack fireworkRocket, double loopScale, float radianSpeed) {
        PyrotechnicsFirework firework = new PyrotechnicsFirework(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, fireworkRocket, loopScale, radianSpeed);
        firework.originalVelocity = new Vec3(
                (level.random.nextDouble() - 0.5) * 0.05,
                (level.random.nextDouble() * 1),
                (level.random.nextDouble() - 0.5) * 0.05);
        level.addFreshEntity(firework);
    }

    public void activate(ItemStack fireworkRocket) {
        activate(fireworkRocket, 0.25, 0.7f);
    }

    public void exploded() {
        if (!fireworks.isEmpty()) {
            for (int i = 0; i < 16 && !this.fireworks.isEmpty(); i++) {
                activate(pop(), 0.8, 0.8f);
            }
        }
    }



    public ItemStack pop() {
        ItemStack shotFirework = this.fireworks.remove(0);
        ItemStack returnFirework = shotFirework.split(1);
        if (!shotFirework.isEmpty()) {
            if (this.fireworks.isEmpty()) {
                this.fireworks.add(shotFirework);
            } else {
                this.fireworks.add((1 + level.random.nextInt(this.fireworks.size())) % (this.fireworks.size() + 1), shotFirework);
            }
        }
        return returnFirework;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FireworksBoxBlockEntity pBlockEntity) {
        if (pBlockEntity.ticks > 10) {
            if (pState.getValue(FireworksBoxBlock.TRIGGERED) && !pBlockEntity.fireworks.isEmpty()) {
                pBlockEntity.activate(pBlockEntity.pop());
            } else if (pState.getValue(FireworksBoxBlock.TRIGGERED)) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(FireworksBoxBlock.TRIGGERED, false).setValue(FireworksBoxBlock.EMPTY, true));
            } else if (pState.getValue(FireworksBoxBlock.FUSE) && !pBlockEntity.fireworks.isEmpty() && !pState.getValue(FireworksBoxBlock.TRIGGERED)) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(FireworksBoxBlock.TRIGGERED, true));
            }
            pBlockEntity.ticks = 0;
        } else if (pState.getValue(FireworksBoxBlock.FUSE) || pState.getValue(FireworksBoxBlock.TRIGGERED)) {
            pBlockEntity.ticks++;
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.fireworks.clear();
        List<ItemStack> initialFireworks = new ArrayList<>();
        for (Tag tag : pTag.getList("Fireworks", Tag.TAG_COMPOUND)) {
            CompoundTag compoundTag = (CompoundTag) tag;
            initialFireworks.add(ItemStack.of(compoundTag));
        }
        Collections.shuffle(initialFireworks);
        this.fireworks.addAll(initialFireworks);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        ListTag fireworksTag = new ListTag();
        for (ItemStack firework : this.fireworks) {
            fireworksTag.add(firework.save(new CompoundTag()));
        }
        pTag.put("Fireworks", fireworksTag);
    }
}
