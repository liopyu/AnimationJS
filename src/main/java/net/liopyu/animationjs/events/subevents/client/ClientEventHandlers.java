package net.liopyu.animationjs.events.subevents.client;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.network.AnimationStateUpdatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.liopyu.animationjs.AnimationJS.MODID;

public class ClientEventHandlers {
    public static final Map<UUID, PlayerRenderer> thisRenderList = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player instanceof AbstractClientPlayer clientPlayer) {
            ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ResourceLocation.fromNamespaceAndPath("zigysplayeranimatorapi", "factory"));
            if (anim == null) {
                return;
            }
            boolean isAnimationActive = anim.isActive();
            AnimationStateUpdatePayload payload = new AnimationStateUpdatePayload(clientPlayer.getUUID(), isAnimationActive);
            PacketDistributor.sendToServer(payload);
        }

    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ResourceLocation.fromNamespaceAndPath(MODID, "animation"),
                42,
                ClientEventHandlers::registerPlayerAnimation);
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

}
