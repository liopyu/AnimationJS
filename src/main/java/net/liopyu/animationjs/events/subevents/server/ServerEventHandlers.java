package net.liopyu.animationjs.events.subevents.server;

import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.events.UniversalController;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandlers {
    public static final Map<UUID, UniversalController> thisList = new HashMap<>();


    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        /*if (event.getEntity() == null) return;
        UUID playerUUID = event.getEntity().getUUID();
        UniversalController controller = thisList.get(playerUUID);
        if (controller == null) {
            ServerPlayer player = AnimationJSHelperClass.getServerPlayerByUUID(playerUUID);
            controller = new UniversalController(player);
            thisList.put(playerUUID, controller);
        }*/
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer && EventHandlers.universalController.hasListeners()) {
            UniversalController cont = ServerEventHandlers.thisList.get(serverPlayer.getUUID());
            if (cont == null) {
                cont = new UniversalController(serverPlayer);
                ServerEventHandlers.thisList.put(serverPlayer.getUUID(), cont);
            }
            EventHandlers.universalController.post(cont);
        }
    }
}
