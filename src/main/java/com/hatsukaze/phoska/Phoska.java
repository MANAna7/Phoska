package com.hatsukaze.phoska;

import com.hatsukaze.phoska.register.ModAccessoriesRegister;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;


@SuppressWarnings("SpellCheckingInspection")
@Mod(Phoska.MODID)
public class Phoska {
    public static final String MODID = "phoska";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Phoska(IEventBus modEventBus, ModContainer modContainer) {
        ModAccessoriesRegister.ITEMS.register(modEventBus);

//        NeoForge.EVENT_BUS.register(this);
    }
}