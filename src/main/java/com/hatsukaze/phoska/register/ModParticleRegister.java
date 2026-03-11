package com.hatsukaze.phoska.register;

import com.hatsukaze.phoska.Phoska;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModParticleRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Phoska.MODID);

    public static final Supplier<SimpleParticleType> NECKLACE_OF_RETURN =
            PARTICLES.register("necklace_of_returning",
                    () -> new SimpleParticleType(false));
}