package net.liopyu.animationjs.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.data.PlayerAnimationData;
import lio.playeranimatorapi.data.PlayerParts;
import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.events.*;
import net.liopyu.animationjs.network.server.AnimationStateTracker;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.forgespi.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
    @Unique
    private static final double POSITION_THRESHOLD = 0.001;
    @Unique
    private static final int COOLDOWN_TICKS = 1;

    @Unique
    private transient Object animatorJS$player = this;
    @Unique
    public final Player animatorJS$objectPlayer = (Player) animatorJS$player;
    @Unique
    private int animatorJS$cooldown;
    @Unique
    private ResourceLocation animatorJS$currentLocation;
    @Unique
    private double animatorJS$prevX;
    @Unique
    private double animatorJS$prevY;
    @Unique
    private double animatorJS$prevZ;

    @Unique
    private boolean animatorJS$isMoving = false;

    @Info("Is the player currently in motion")
    public boolean animatorJS$isMoving() {
        return animatorJS$isMoving;
    }

    @Unique
    private void animatorJS$movingBoolean() {
        Player player = (Player) animatorJS$player;
        if (player != null) {
            double currentX = player.getX();
            double currentY = player.getY();
            double currentZ = player.getZ();

            double deltaX = Math.abs(animatorJS$prevX - currentX);
            double deltaY = Math.abs(animatorJS$prevY - currentY);
            double deltaZ = Math.abs(animatorJS$prevZ - currentZ);

            boolean movingX = deltaX > POSITION_THRESHOLD;
            boolean movingY = deltaY > POSITION_THRESHOLD;
            boolean movingZ = deltaZ > POSITION_THRESHOLD;

            animatorJS$prevX = currentX;
            animatorJS$prevY = currentY;
            animatorJS$prevZ = currentZ;

            if (movingX || movingY || movingZ) {
                animatorJS$cooldown = COOLDOWN_TICKS;
                animatorJS$isMoving = true;
                return;
            } else {
                if (animatorJS$cooldown > 0) {
                    animatorJS$cooldown--;
                    animatorJS$isMoving = true;
                    return;
                } else {
                    animatorJS$isMoving = false;
                    return;
                }
            }
        }
        animatorJS$isMoving = false;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void animationJS$tick(CallbackInfo ci) {
        animatorJS$movingBoolean();
    }

    @Unique
    @Info("Determines if a playerAnimator animation is currently playing")
    public boolean animatorJS$isAnimActive() {
        return AnimationStateTracker.getAnimationState(animatorJS$objectPlayer.getUUID());
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
            Used to trigger animations off a server player. This can be
            called from any server player object.
                        
            Example Usage:
            ```javascript
            event.player.triggerAnimation("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json")
    })
    public void animatorJS$triggerAnimation(Object animationName) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animatorJS$objectPlayer.level().isClientSide()) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Unable to play animations from client scripts. Please use server scripts or the AnimationJS.universalController() server event.");
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
            Used to trigger animations off a server player. This can be
            called from any server player object with the extra option for animations to overlap themselves.
                        
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
        if (animatorJS$objectPlayer.level().isClientSide()) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Unable to play animations from client scripts. Please use server scripts or the AnimationJS.universalController() server event.");
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
            Used to trigger animations off the server player with customizable animation data.
                        
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
        if (animatorJS$objectPlayer.level().isClientSide()) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Unable to play animations from client scripts. Please use server scripts or the AnimationJS.universalController() server event.");
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
