package net.liopyu.animationjs.events.subevents.server;

import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.IAnimationTrigger;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.events.UniversalController;
import net.liopyu.animationjs.mixin.PlayerAnimationJSMixin;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandlers {
    public static final Map<UUID, UniversalController> thisList = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        animatorJS$updateMovementBoolean(event.player);
        if (event.player instanceof ServerPlayer serverPlayer && EventHandlers.universalController.hasListeners()) {
            UniversalController cont = ServerEventHandlers.thisList.get(serverPlayer.getUUID());
            if (cont == null) {
                cont = new UniversalController(serverPlayer);
                ServerEventHandlers.thisList.put(serverPlayer.getUUID(), cont);
            }
            EventHandlers.universalController.post(cont);
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
