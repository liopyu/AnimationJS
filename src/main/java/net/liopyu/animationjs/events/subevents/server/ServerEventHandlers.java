package net.liopyu.animationjs.events.subevents.server;

import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.IAnimationTrigger;
import net.liopyu.animationjs.events.UniversalController;
import net.liopyu.animationjs.network.server.AnimationStateTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerEventHandlers {
    public static final Map<UUID, UniversalController> thisList = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        animatorJS$updateMovementBoolean(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer serverPlayer && EventHandlers.universalController.hasListeners()) {
            UniversalController cont = ServerEventHandlers.thisList.get(serverPlayer.getUUID());
            if (cont == null) {
                cont = new UniversalController(serverPlayer);
                ServerEventHandlers.thisList.put(serverPlayer.getUUID(), cont);
            }
            EventHandlers.universalController.post(cont);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AnimationStateTracker.removePlayer(player);
        }
    }

    public static void animatorJS$updateMovementBoolean(Player player) {
        if (player instanceof IAnimationTrigger accessor) {

            double currentX = player.getX();
            double currentY = player.getY();
            double currentZ = player.getZ();

            double deltaX = Math.abs(accessor.animatorJS$getPrevX() - currentX);
            double deltaY = Math.abs(accessor.animatorJS$getPrevY() - currentY);
            double deltaZ = Math.abs(accessor.animatorJS$getPrevZ() - currentZ);

            boolean movingX = deltaX > 0.001;
            boolean movingY = deltaY > 0.001;
            boolean movingZ = deltaZ > 0.001;

            accessor.animatorJS$setPrevX(currentX);
            accessor.animatorJS$setPrevY(currentY);
            accessor.animatorJS$setPrevZ(currentZ);

            if (movingX || movingY || movingZ) {
                accessor.animatorJS$setCooldown(1);
                accessor.animatorJS$setIsMoving(true);
            } else {
                if (accessor.animatorJS$getCooldown() > 0) {
                    accessor.animatorJS$setCooldown(accessor.animatorJS$getCooldown() - 1);
                    accessor.animatorJS$setIsMoving(true);
                } else {
                    accessor.animatorJS$setIsMoving(false);
                }
            }
        }
    }
}
