package com.hatsukaze.phoska;

import com.hatsukaze.phoska.register.ModAccessoriesRegister;
import com.hatsukaze.phoska.register.ModNetworkRegister;
import com.hatsukaze.phoska.register.ModParticleRegister;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.beans.EventHandler;


@SuppressWarnings("SpellCheckingInspection")
@Mod(Phoska.MODID)
public class Phoska {
    public static final String MODID = "phoska";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Phoska(IEventBus modEventBus, ModContainer modContainer) {
        ModAccessoriesRegister.ITEMS.register(modEventBus);
        ModParticleRegister.PARTICLES.register(modEventBus);
        ModNetworkRegister.register(modEventBus);

//        modEventBus.addListener(PhoskaClientHandler::registerParticles);
    }
}