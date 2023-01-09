package net.soko.pyrotechnics.entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PyroTechnics.MOD_ID);

    public static final RegistryObject<EntityType<PyrotechnicsFirework>> PYROTECHNICS_FIREWORK = ENTITY_TYPES.register("pyrotechnics_firework",
            () -> EntityType.Builder.<PyrotechnicsFirework>of(PyrotechnicsFirework::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("pyrotechnics_firework"));

    public static final RegistryObject<EntityType<PyrotechnicsLargeFireCharge>> PYROTECHNICS_LARGE_FIRE_CHARGE = ENTITY_TYPES.register("large_fire_charge",
            () -> EntityType.Builder.<PyrotechnicsLargeFireCharge>of(PyrotechnicsLargeFireCharge::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("large_fire_charge"));
}
