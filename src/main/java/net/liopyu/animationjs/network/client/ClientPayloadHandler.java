package net.liopyu.animationjs.network.client;

import net.liopyu.animationjs.network.AnimationStateUpdatePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleAnimationStateUpdate(AnimationStateUpdatePayload payload, IPayloadContext context) {
        Minecraft.getInstance().execute(() -> {
            // Handle animation state update on the client
        });
    }
}


