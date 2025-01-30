package net.liopyu.animationjs.mixin;

/*import dev.kosmx.playerAnim.core.util.Ease;*/

import com.zigythebird.playeranimatorapi.API.PlayerAnimAPI;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.modifier.CommonModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.liopyu.animationjs.events.IAnimationTrigger;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = Player.class, remap = true)
public abstract class PlayerAnimationJSMixin implements IAnimationTrigger {
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

    public void animatorJS$setIsMoving(boolean b) {
        animatorJS$isMoving = b;
    }

    public double animatorJS$getPrevX() {
        return animatorJS$prevX;
    }

    public double animatorJS$getPrevY() {
        return animatorJS$prevY;
    }

    public double animatorJS$getPrevZ() {
        return animatorJS$prevZ;
    }

    public void animatorJS$setPrevX(double d) {
        animatorJS$prevX = d;
    }

    public void animatorJS$setPrevY(double d) {
        animatorJS$prevY = d;
    }

    public void animatorJS$setPrevZ(double d) {
        animatorJS$prevZ = d;
    }

    public int animatorJS$getCooldown() {
        return animatorJS$cooldown;
    }

    public void animatorJS$setCooldown(int i) {
        animatorJS$cooldown = i;
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
            ResourceLocation aN = (ResourceLocation) animName;
            if (animatorJS$canPlay(aN)) {
                PlayerAnimAPI.playPlayerAnim(serverPlayer.serverLevel(), serverPlayer, aN);
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
            event.player.triggerAnimation("animationjs:waving", 1, "linear", 1, 0);
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "priority", value = "int: The priority level of the animation (higher value means higher priority)"),
            @Param(name = "startTick", value = "int: The tick delay before the animation starts")
    })
    public void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, int priority, int startTick) {
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
            ResourceLocation aN = (ResourceLocation) animName;
            if (animatorJS$canPlay(aN)) {
                int easingID = ((Ease) ease).getId();
                ServerLevel serverLevel = serverPlayer.serverLevel();
                PlayerAnimationData data = new PlayerAnimationData(
                        serverPlayer.getUUID(),
                        aN,
                        PlayerParts.allEnabled,
                        null,
                        transitionLength,
                        easingID,
                        priority,
                        startTick
                );
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, data);
            }
        }
    }


    @Unique
    @Info(value = """
            Used to trigger animations off the server player with customizable animation data.
            
            Example Usage:
            ```javascript
            event.triggerAnimation("animationjs:waving", 1, "linear", 1, 0, ["playeranimatorapi:mirroronalthand"], parts => {
            	parts.leftArm.setEnabled(false);
            });
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "priority", value = "int: The priority level of the animation (higher value means higher priority)"),
            @Param(name = "startTick", value = "int: The tick delay before the animation starts"),
            @Param(name = "modifiers", value = "List<String>: List of modifiers to apply to the animation, can also be null"),
            @Param(name = "partsConsumer", value = "Consumer<PlayerParts>: Consumer to modify player parts such as part visibility, rotation, etc.")
    })
    public void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, int priority, int startTick, List<?> modifiers, Consumer<PlayerParts> partsConsumer) {
        if (animatorJS$objectPlayer.level().isClientSide()) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Unable to play animations from client scripts. Please use server scripts or the AnimationJS.universalController() server event.");
        } else if (animatorJS$player instanceof ServerPlayer serverPlayer) {
            Object animName = AnimationJSHelperClass.convertObjectToDesired(animationID, "resourcelocation");
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
            if (ease == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: triggerAnimation. Must be an easing type. Example: \"LINEAR\"");
                return;
            }
            Object modifierlist = AnimationJSHelperClass.convertObjectToDesired(modifiers, "modifierlist");
            if (modifierlist == null && modifiers != null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid modifiers in field: triggerAnimation. Must be a string list of modifiers or null.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            if (animatorJS$canPlay(aN)) {
                int easingID = ((Ease) ease).getId();
                PlayerParts playerParts = new PlayerParts();
                if (partsConsumer != null) {
                    try {
                        partsConsumer.accept(playerParts);
                    } catch (Exception t) {
                        AnimationJSHelperClass.logServerErrorMessageOnceCatchable("[AnimationJS]: Error in AnimationJS.universalController for method triggerAnimation", t);
                    }
                }
                ServerLevel serverLevel = serverPlayer.serverLevel();
                PlayerAnimationData data = new PlayerAnimationData(
                        serverPlayer.getUUID(),
                        aN,
                        playerParts,
                        (List<CommonModifier>) modifierlist,
                        transitionLength,
                        easingID,
                        priority,
                        startTick
                );
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, data);
            }
        }
    }


    @Unique
    @Info(value = """
            Used to stop a certain player animation.
            
            Example Usage:
            ```javascript
            event.stopAnimation("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
    })
    public void animatorJS$stopAnimation(Object animationName) {
        if (animatorJS$objectPlayer.level().isClientSide()) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Unable to play animations from client scripts. Please use server scripts or the AnimationJS.universalController() server event.");
        } else if (animatorJS$player instanceof ServerPlayer serverPlayer) {
            Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
                return;
            }
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimAPI.stopPlayerAnim(serverLevel, serverPlayer, aN);
        }
    }
}
