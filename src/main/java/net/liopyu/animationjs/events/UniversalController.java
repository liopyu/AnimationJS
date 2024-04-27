package net.liopyu.animationjs.events;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.data.PlayerAnimationData;
import lio.playeranimatorapi.data.PlayerParts;
import lio.playeranimatorapi.modifier.CommonModifier;
import net.liopyu.animationjs.network.server.AnimationStateTracker;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class UniversalController extends SimplePlayerEventJS {


    private transient ResourceLocation currentLocation;

    public UniversalController(Player p) {
        super(p);
    }

    private boolean canPlay(ResourceLocation aN, Player player) {
        if (currentLocation == null) {
            currentLocation = aN;
            return true;
        }
        if (!isAnimActive(player)) {
            currentLocation = aN;
            return true;
        } else if (!currentLocation.toString().equals(aN.toString())) {
            currentLocation = aN;
            return true;
        }
        return false;
    }

    public boolean isAnimActive(Player player) {
        UUID playerUUID = player.getUUID();
        if (player.getServer().isDedicatedServer()) {
            return AnimationStateTracker.getAnimationState(playerUUID);
        } else {
            var clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(playerUUID);
            if (clientPlayer != null) {
                ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(new ResourceLocation("liosplayeranimatorapi", "factory"));
                if (anim == null) {
                    return false;
                }
                boolean isAnimationActive = anim.isActive();
                return isAnimationActive;
            }
            return false;
        }
    }

    private ServerPlayer getServerPlayer() {
        if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
            return serverPlayer;
        }
        return null;
    }

    @Info(value = """
            Used to play animations on player tick.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
    })
    public void startAnimation(Object animationName) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: startAnimation. Must be a ResourceLocation.");
            return;
        }
        ResourceLocation aN = (ResourceLocation) animName;
        if (canPlay(aN, this.getPlayer())) {
            PlayerAnimAPI.playPlayerAnim(getServerPlayer().serverLevel(), getServerPlayer(), aN);
        }
    }

    @Info(value = """
            Used to play animations on player tick with the option
            to have animations overlap themselves when played.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving", true)
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "canOverlapSelf", value = "Boolean: Whether the animation can overlap itself if it's already playing")
    })
    public void startAnimation(Object animationName, boolean canOverlapSelf) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: startAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        if (canOverlapSelf) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
        } else if (canPlay(aN, this.getPlayer())) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
        }
    }

    @Info(value = """
            Used to play animations on player tick with customizable animation data.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving", event.player, 1, "linear", true, false);
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "firstPersonEnabled", value = "boolean: Whether the animation should be visible in first-person view"),
            @Param(name = "important", value = "boolean: Whether the animation is important and should override other animations")
    })
    public void startAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationID, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: startAnimation. Must be a ResourceLocation.");
            return;
        }
        Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
        if (ease == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: startAnimation. Must be an easing type. Example: \"LINEAR\"");
            return;
        }
        ResourceLocation aN = (ResourceLocation) animName;
        if (canPlay(aN, this.getPlayer())) {
            int easingID = ((Ease) ease).getId();
            ServerLevel serverLevel = getServerPlayer().serverLevel();
            PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), data);
        }
    }

    @Info(value = """
            Used to play animations on player tick with customizable animation data.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving", event.player, 1, "linear", true, false, ["playeranimatorapi:mirroronalthand"], parts => {
            	parts.leftArm.setEnabled(false)
            });
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "firstPersonEnabled", value = "boolean: Whether the animation should be visible in first-person view"),
            @Param(name = "important", value = "boolean: Whether the animation is important and should override other animations"),
            @Param(name = "modifiers", value = "List<String>: List of modifiers to apply to the animation"),
            @Param(name = "partsConsumer", value = "Consumer<PlayerParts>: Consumer to modify player parts such as part visibility, rotation ect.")
    })
    public void startAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important, List<?> modifiers, Consumer<PlayerParts> partsConsumer) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationID, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: startAnimation. Must be a ResourceLocation.");
            return;
        }
        Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
        if (ease == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: startAnimation. Must be an easing type. Example: \"LINEAR\"");
            return;
        }
        Object modifierlist = AnimationJSHelperClass.convertObjectToDesired(modifiers, "modifierlist");
        if (modifierlist == null && modifiers != null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid modifiers in field: startAnimation. Must be a string list of modifiers or null.");
            return;
        }
        ResourceLocation aN = (ResourceLocation) animName;
        if (canPlay(aN, this.getPlayer())) {
            int easingID = ((Ease) ease).getId();
            PlayerParts playerParts = new PlayerParts();
            if (partsConsumer != null) {
                try {
                    partsConsumer.accept(playerParts);
                } catch (Exception t) {
                    AnimationJSHelperClass.logServerErrorMessageOnceCatchable("[AnimationJS]: Error in AnimationJS.universalController for method startAnimation", t);
                }
            }
            ServerLevel serverLevel = getServerPlayer().serverLevel();
            PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, playerParts, (List<CommonModifier>) modifierlist, transitionLength, easingID, firstPersonEnabled, important);
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), data);
        }
    }

    @Info(value = """
            Used to stop a certain player animation.
                        
            Example Usage:
            ```javascript
            event.stopAnimation("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
    })
    public void stopAnimation(Object animationName) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        PlayerAnimAPI.stopPlayerAnim(serverLevel, getServerPlayer(), aN);
    }
}

