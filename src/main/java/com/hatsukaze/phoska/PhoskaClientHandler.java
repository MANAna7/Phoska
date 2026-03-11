package com.hatsukaze.phoska;

import com.hatsukaze.phoska.effect.NecklaceReturnParticle;
import com.hatsukaze.phoska.register.ModParticleRegister;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import static com.hatsukaze.phoska.Phoska.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class PhoskaClientHandler {

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleRegister.NECKLACE_OF_RETURN.get(),
                NecklaceReturnParticle.Provider::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Phoska.LOGGER.info("client setup");
    }
}
