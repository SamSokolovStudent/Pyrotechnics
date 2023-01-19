package net.soko.pyrotechnics.data.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;

public class ModLootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, PyroTechnics.MOD_ID);

    public static final RegistryObject<LootItemConditionType> IS_EXPLOSION =
            LOOT_CONDITIONS.register("is_explosion",
                    () -> new LootItemConditionType(new IsExplosionCondition.ExplosionSerializer()));
}
