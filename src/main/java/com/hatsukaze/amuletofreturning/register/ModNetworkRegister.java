package com.hatsukaze.amuletofreturning.register;

import com.hatsukaze.amuletofreturning.AmuletOfReturningMain;
import com.hatsukaze.amuletofreturning.network.AmuletOfReturningActivatedPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworkRegister {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworkRegister::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(AmuletOfReturningMain.MODID);
        registrar.playToClient(
                AmuletOfReturningActivatedPayload.TYPE,
                AmuletOfReturningActivatedPayload.STREAM_CODEC,
                (payload, context) -> {
                    // ラムダ内で完全修飾名を使うことで、サーバー側でクラスロードされない
                    context.enqueueWork(() ->
                            com.hatsukaze.amuletofreturning.network.ModClientPayloadHandler.handle(payload, context)
                    );
                }
        );
    }
}