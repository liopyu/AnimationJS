package net.liopyu.animationjs.events;

import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SubEventsServer {
    public static final Map<UUID, UniversalController> thisList = new HashMap<>();

    /*@SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() == null) return;
        UUID playerUUID = event.getEntity().getUUID();
        UniversalController controller = thisList.get(playerUUID);
        if (controller == null) {
            ServerPlayer player = AnimationJSHelperClass.getServerPlayerByUUID(playerUUID);
            controller = new UniversalController(player);
            thisList.put(playerUUID, controller);
        }
    }*/

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (EventHandlers.universalController.hasListeners()) {
            //UniversalController cont = SubEventsServer.thisList.get(event.player.getUUID());
            UniversalController cont = new UniversalController(event.player);
            EventHandlers.universalController.post(cont);
        }
    }
}
