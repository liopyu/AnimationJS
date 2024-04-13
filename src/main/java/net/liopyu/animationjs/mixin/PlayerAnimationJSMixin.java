package net.liopyu.animationjs.mixin;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.API.PlayerAnimAPIClient;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.IAnimationTrigger;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
    @Info(value = """
            Used to trigger animations on both server/client. This can be
            called from both the client or server player object.
                        
            Example Usage:
            ```javascript
            event.player.triggerAnimation("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json")
    })
    public void animatorJS$triggerAnimation(Object animationName) {
        Object obj = this;
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (obj instanceof AbstractClientPlayer player) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimAPIClient.playPlayerAnim(player, aN);
        } else if (obj instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            ServerLevel serverLevel = serverPlayer.serverLevel();
            PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, aN);
        }
    }

}
