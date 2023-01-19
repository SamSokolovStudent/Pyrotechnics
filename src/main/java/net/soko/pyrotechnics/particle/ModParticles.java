package net.soko.pyrotechnics.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.soko.pyrotechnics.PyroTechnics;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, PyroTechnics.MOD_ID);

    public static final RegistryObject<SimpleParticleType> EMBER =
            PARTICLE_TYPES.register("ember", () -> new SimpleParticleType(false));
}
