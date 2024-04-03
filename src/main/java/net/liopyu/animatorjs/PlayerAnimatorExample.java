package net.liopyu.animatorjs;


import dev.architectury.platform.Platform;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forgespi.Environment;

import static net.liopyu.animatorjs.AnimatorJS.MODID;

/**
 * This is an example implementation of PlayerAnimator resourceLoading and playerMapping
 */
@Mod.EventBusSubscriber(modid = AnimatorJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerAnimatorExample {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(AnimatorJS.MODID, "animation"),
                42,
                PlayerAnimatorExample::registerPlayerAnimation);
    }

    private static IAnimation registerPlayerAnimation(Player player) {
        return new ModifierLayer<>();
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
        try {
            PlayerAnimationTrigger.triggerAnimation(event.getEntity(), "waving");
        } catch (Exception e) {
            ConsoleJS.SERVER.error("Failed to trigger animation on server: " + e);
        }
    }
}
