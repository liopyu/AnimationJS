package net.liopyu.animationjs;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerAnimationTrigger {
    /*public static void triggerAnimation(Player player, String animationName) {
        Optional<KeyframeAnimation> emote = AbstractServerEmotePlay.getLoadedEmotes().entrySet().stream().filter(it -> it.getValue().extraData.get("name").equals(animationName)).findFirst().map(Map.Entry::getValue);
        if (emote.isPresent()) {
            var e = emote.get();
            ServerEmoteAPI.setPlayerPlayingEmote(player.getUUID(), e);
        }
    }*/
    @Info(value = """
            Used to trigger animations on the client.
                        
            Example Usage:
            ```javascript
            ClientEvents.tick(event => {
                if (event.player.isShiftKeyDown()) {
                    event.player.triggerAnimation(event.player, "animationjs:waving", "animationjs:animation")
                }
            })
            ```
            """, params = {
            @Param(name = "player", value = "Player Object: The client player being animated"),
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "animationLocation", value = "ResourceLocation: This is the registry name of the registered animation.")
    })
    public static void triggerAnimation(AbstractClientPlayer player, Object animationName, Object animationLocation) {
        if (player == null) {
            AnimationJSHelperClass.logErrorMessageOnce("[AnimationJS]: Player is null in field: triggerAnimation");
            return;
        }
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        Object animationLoc = AnimationJSHelperClass.convertObjectToDesired(animationLocation, "resourcelocation");
        if (animationLoc != null) {
            var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get((ResourceLocation) animationLoc);
            if (animation != null) {
                if (animName != null) {
                    animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation((ResourceLocation) animName)));
                } else {
                    AnimationJSHelperClass.logErrorMessageOnce("[AnimationJS]: Incorrect value for animation name in field: triggerAnimation. Must be a ResourceLocation.");
                }
            }
        } else {
            AnimationJSHelperClass.logErrorMessageOnce("[AnimationJS]: Incorrect value for animation location in field: triggerAnimation. Must be a ResourceLocation.");
        }
    }

}
