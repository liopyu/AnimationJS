package net.liopyu.animationjs.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.IAnimationTrigger;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
    @Info(value = """
            Used to trigger animations on the client via the client player.
                        
            Example Usage:
            ```javascript
            ClientEvents.tick(event => {
                if (event.player.isShiftKeyDown()) {
                    event.player.triggerAnimation("animationjs:waving")
                }
            })
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json")
    })
    public void animatorJS$triggerAnimation(Object animationName) {
        Object obj = this;
        if (obj instanceof AbstractClientPlayer player) {
            Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
            var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(new ResourceLocation(AnimationJS.MODID, "animation"));
            if (animation != null) {
                if (animName != null) {
                    animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation((ResourceLocation) animName)));
                } else {
                    AnimationJSHelperClass.logErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                }
            }
        } else {
            AnimationJSHelperClass.logErrorMessageOnce("[AnimationJS]: Incorrect value for clientPlayer in field: triggerAnimation. Must be an AbstractClientPlayer.");
        }
    }
}
