package net.liopyu.animationjs.network;

import net.liopyu.animationjs.network.client.ClientPayloadHandler;
import net.liopyu.animationjs.network.server.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "animationjs", bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                AnimationStateUpdatePayload.TYPE,
                AnimationStateUpdatePayload.CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleAnimationStateUpdate,
                        ServerPayloadHandler::handleAnimationStateUpdate
                )
        );
    }
}
