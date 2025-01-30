package net.liopyu.animationjs.network;


import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record AnimationStateUpdatePayload(UUID playerUUID, boolean isActive) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnimationStateUpdatePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("animationjs", "animation_state_update"));

    public static final StreamCodec<ByteBuf, AnimationStateUpdatePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            AnimationStateUpdatePayload::playerUUID,
            ByteBufCodecs.BOOL,
            AnimationStateUpdatePayload::isActive,
            AnimationStateUpdatePayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
