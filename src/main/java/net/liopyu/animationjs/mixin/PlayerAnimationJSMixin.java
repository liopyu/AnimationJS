package net.liopyu.animationjs.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.API.PlayerAnimAPIClient;
import lio.playeranimatorapi.data.PlayerAnimationData;
import lio.playeranimatorapi.data.PlayerParts;
import net.liopyu.animationjs.events.Animations;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.IAnimationTrigger;
import net.liopyu.animationjs.events.UniversalController;
import net.liopyu.animationjs.utils.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Consumer;

@Mixin(Player.class)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
    @Unique
    private final Object animatorJS$player = this;
    @Unique
    private final Map<UUID, UniversalController> animatorJS$playerControllers = new HashMap<>();

    @Unique
    private AnimationJSHelperClass.EntityMovementTracker animatorJS$movementTracker = new AnimationJSHelperClass.EntityMovementTracker();
    @Unique
    private ResourceLocation animatorJS$currentLocation;

    @Unique
    @Info("Is the player currently in motion")
    public boolean animatorJS$isMoving() {
        return animatorJS$movementTracker.isMoving((Entity) animatorJS$player);
    }

    @Unique
    private AbstractClientPlayer animatorJS$getClientPlayer() {
        if (animatorJS$player != null) {
            return AnimationJSHelperClass.getClientPlayerByUUID(((Player) animatorJS$player).getUUID());
        } else return null;
    }

    @Unique
    private IAnimation animatorJS$getAnim() {
        if (animatorJS$getClientPlayer() == null) return null;
        ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(animatorJS$getClientPlayer()).get(new ResourceLocation("liosplayeranimatorapi", "factory"));
        if (anim != null) {
            return anim.getAnimation();
        } else return null;
    }

    @Unique
    @Info("Determines if a playerAnimator animation is currently playing")
    public boolean animatorJS$isAnimActive() {
        return animatorJS$getAnim() != null && animatorJS$getAnim().isActive();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void animationJS$tick(CallbackInfo ci) {
        if (!animatorJS$isAnimActive()) {
            animatorJS$currentLocation = null;
        }
        if (animatorJS$player != null && animatorJS$player instanceof ServerPlayer serverPlayer && EventHandlers.universalController.hasListeners()) {
            UniversalController controller = animatorJS$playerControllers.computeIfAbsent(serverPlayer.getUUID(), uuid -> new UniversalController(serverPlayer));
            EventHandlers.universalController.post(controller);
        }
    }


    @Unique
    private boolean animatorJS$canPlay(ResourceLocation aN) {
        if (this.animatorJS$currentLocation == null) {
            this.animatorJS$currentLocation = aN;
            return true;
        }
        if (!animatorJS$isAnimActive()) {
            this.animatorJS$currentLocation = aN;
            return true;
        } else if (!this.animatorJS$currentLocation.toString().equals(aN.toString())) {
            this.animatorJS$currentLocation = aN;
            return true;
        }
        return false;
    }

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
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animatorJS$player instanceof AbstractClientPlayer player) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            if (animatorJS$canPlay(aN)) {
                PlayerAnimAPIClient.playPlayerAnim(player, aN);
            }
        } else if (animatorJS$player instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            if (animatorJS$canPlay(aN)) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, aN);
            }
        }
    }

    @Unique
    @Info(value = """
            Used to trigger animations on both server/client. This can be
            called from both the client or server player object.
                        
            Example Usage:
            ```javascript
            event.player.triggerAnimation("animationjs:waving", true)
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "canOverlapSelf", value = "Boolean: Whether the animation can overlap itself if it's already playing")
    })
    public void animatorJS$triggerAnimation(Object animationName, boolean canOverlapSelf) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animatorJS$player instanceof AbstractClientPlayer player) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            if (canOverlapSelf) {
                PlayerAnimAPIClient.playPlayerAnim(player, aN);
            } else if (animatorJS$canPlay(aN)) {
                PlayerAnimAPIClient.playPlayerAnim(player, aN);
            }
        } else if (animatorJS$player instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            if (canOverlapSelf) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, aN);
            } else if (animatorJS$canPlay(aN)) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, aN);
            }
        }
    }

    @Unique
    @Info(value = """
            Used to trigger animations on both server/client with customizable animation data.
                        
            Example Usage:
            ```javascript
            event.player.triggerAnimation("animationjs:waving", 1, "linear", true, false);
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "firstPersonEnabled", value = "boolean: Whether the animation should be visible in first-person view"),
            @Param(name = "important", value = "boolean: Whether the animation is important and should override other animations")
    })
    public void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationID, "resourcelocation");
        if (animatorJS$player instanceof AbstractClientPlayer player) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
            if (ease == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid easeID in field: triggerAnimation. Must be an easing type. Example: \"LINEAR\"");
                return;
            }
            int easingID = ((Ease) ease).getId();
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimationData data = new PlayerAnimationData(player.getUUID(), aN, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);

            if (animatorJS$canPlay(aN)) {
                PlayerAnimAPIClient.playPlayerAnim(player, data);
            }
        } else if (animatorJS$player instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
            if (ease == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: triggerAnimation. Must be an easing type. Example: \"LINEAR\"");
                return;
            }
            int easingID = ((Ease) ease).getId();
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimationData data = new PlayerAnimationData(serverPlayer.getUUID(), aN, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);
            if (animatorJS$canPlay(aN)) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, data);
            }
        }
    }


}
