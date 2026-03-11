package com.hatsukaze.phoska.register;

import com.hatsukaze.phoska.Phoska;
import com.hatsukaze.phoska.network.ModClientPayloadHandler;
import com.hatsukaze.phoska.network.NecklaceOfReturningActivatedPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworkRegister {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworkRegister::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Phoska.MODID);
        registrar.playToClient(
                NecklaceOfReturningActivatedPayload.TYPE,
                NecklaceOfReturningActivatedPayload.STREAM_CODEC,
                ModClientPayloadHandler::handle
        );
    }
}
