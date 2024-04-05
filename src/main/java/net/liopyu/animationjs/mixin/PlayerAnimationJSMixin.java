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
import net.liopyu.animationjs.network.packet.AnimationPacketHandler;
import net.liopyu.animationjs.utils.IAnimationTrigger;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
    @Info(value = """
            Used to trigger animations on the client side. This can be
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
            var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(new ResourceLocation(AnimationJS.MODID, "animation"));
            if (animation != null) {
                animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation((ResourceLocation) animName)));
            }
        } else if (obj instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            UUID playerUUID = serverPlayer.getUUID();
            ResourceLocation aN = (ResourceLocation) animName;
            AnimationPacketHandler.Message packet = new AnimationPacketHandler.Message(playerUUID, aN);
            AnimationJS.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }
}
