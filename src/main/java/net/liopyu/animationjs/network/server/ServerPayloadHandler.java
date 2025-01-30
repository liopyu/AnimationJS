package net.liopyu.animationjs.network.server;

import net.liopyu.animationjs.network.AnimationStateUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handleAnimationStateUpdate(AnimationStateUpdatePayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (player != null) {
            player.getServer().execute(() -> {
                AnimationStateTracker.setAnimationState(player, payload.isActive());
            });
        }
    }
}