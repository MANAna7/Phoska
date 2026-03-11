package com.hatsukaze.phoska.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@OnlyIn(Dist.CLIENT)
public class ModClientPayloadHandler {
    public static int overlayTicks = 0;

    public static void handle(NecklaceOfReturningActivatedPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            overlayTicks = 60; // 3秒
        });
    }
}
