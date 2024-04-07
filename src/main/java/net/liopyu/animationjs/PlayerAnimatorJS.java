package net.liopyu.animationjs;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.liopyu.animationjs.AnimationJS.MODID;

/**
 * This is an example implementation of PlayerAnimator resourceLoading and playerMapping
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerAnimatorJS {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        //Set the player construct callback. It can be a lambda function.
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(MODID, "animation"),
                42,
                PlayerAnimatorJS::registerPlayerAnimation);
    }

    //This method will set your mods animation into the library.
    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        //This will be invoked for every new player
        return new ModifierLayer<>();
    }

    /*@SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
        try {

        } catch (Exception e) {
            ConsoleJS.SERVER.error("Failed to trigger animation on server: " + e);
        }
    }*/
    /*@SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(AnimationJS.MODID, "animation"),
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
    }*/
}
