package net.soko.pyrotechnics.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.soko.pyrotechnics.PyroTechnics;

public class ModLootContextParams {
    public static final LootContextParam<Explosion.BlockInteraction> EXPLOSION_BLOCK_INTERACTION = new LootContextParam<>(new ResourceLocation(PyroTechnics.MOD_ID,"explosion_block_interaction"));
}
