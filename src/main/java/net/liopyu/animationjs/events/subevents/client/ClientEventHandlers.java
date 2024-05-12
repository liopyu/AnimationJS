package net.liopyu.animationjs.events.subevents.client;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.network.NetworkHandler;
import net.liopyu.animationjs.network.packet.AnimationStateUpdatePacket;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.liopyu.animationjs.AnimationJS.MODID;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandlers {

    public static final Map<UUID, PlayerRenderer> thisRenderList = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof AbstractClientPlayer clientPlayer) {
            ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(new ResourceLocation("liosplayeranimatorapi", "factory"));
            if (anim == null) {
                return;
            }
            boolean isAnimationActive = anim.isActive();
            AnimationStateUpdatePacket packet = new AnimationStateUpdatePacket(clientPlayer.getUUID(), isAnimationActive);
            NetworkHandler.sendToServer(packet);
        }

    }


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(MODID, "animation"),
                42,
                ClientEventHandlers::registerPlayerAnimation);
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }
}