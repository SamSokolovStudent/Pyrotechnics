package net.soko.pyrotechnics.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PyroTechnics.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> FIREWORKS_BOX_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("fireworks_box", () -> new SimpleCraftingRecipeSerializer<>(FireworkBoxRecipe::new));

}
