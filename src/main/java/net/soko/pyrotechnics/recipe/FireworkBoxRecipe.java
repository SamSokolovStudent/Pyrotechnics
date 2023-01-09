package net.soko.pyrotechnics.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.soko.pyrotechnics.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class FireworkBoxRecipe extends CustomRecipe {
    private static final int[] FIREWORK_SLOTS = new int[]{0, 1, 2, 4};
    private static final int[] PAPER_SLOTS = new int[]{3, 5, 6, 7, 8};
    public FireworkBoxRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        for (int i : PAPER_SLOTS) {
            if (!pContainer.getItem(i).is(Items.PAPER)) {
                return false;
            }
        }
        for (int i: FIREWORK_SLOTS) {
            if (pContainer.getItem(i).getItem() instanceof FireworkRocketItem) {
                return true;
            } else if (!pContainer.getItem(i).isEmpty()) {
                return false;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        List<ItemStack> fireworks = new ArrayList<>();
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            if (pContainer.getItem(i).getItem() instanceof FireworkRocketItem) {
                fireworks.add(pContainer.getItem(i).copy());

            }
        }
        CompoundTag tag = new CompoundTag();
        ListTag fireworksTag = new ListTag();
        for (ItemStack firework : fireworks) {
            fireworksTag.add(firework.save(new CompoundTag()));
        }
        tag.put("Fireworks", fireworksTag);
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.put("BlockEntityTag", tag);
        ItemStack fireworkBox = new ItemStack(ModItems.FIREWORKS_BOX.get());
        fireworkBox.setTag(blockEntityTag);
        return fireworkBox;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pContainer) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(pContainer);
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.getItem() instanceof FireworkRocketItem) {
                pContainer.setItem(i, item.copyWithCount(1));
            }
        }
        return remainingItems;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FIREWORKS_BOX_RECIPE_SERIALIZER.get();
    }
}
