package com.hatsukaze.phoska.network;

import com.hatsukaze.phoska.Phoska;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NecklaceOfReturningActivatedPayload() implements CustomPacketPayload {

        public static final Type<NecklaceOfReturningActivatedPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(Phoska.MODID, "necklace_of_returning_activated"));

        public static final StreamCodec<ByteBuf, NecklaceOfReturningActivatedPayload> STREAM_CODEC =
                StreamCodec.unit(new NecklaceOfReturningActivatedPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
}

