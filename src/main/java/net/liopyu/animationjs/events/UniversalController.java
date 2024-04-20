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
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class UniversalController extends SimplePlayerEventJS {
    private transient ResourceLocation currentLocation;


    public UniversalController(Player p) {
        super(p);
    }

    private ServerPlayer getServerPlayer() {
        if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
            return serverPlayer;
        }
        return null;
    }

    @Info(value = """
            Used to trigger animations on both server/client on player tick with the option
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
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(getServerPlayer().getUUID());
        if (canOverlapSelf) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
        } else if (canPlay(aN, clientPlayer)) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
        }
    }

    @Info(value = """
            Used to trigger animations on both server/client with customizable animation data.
                        
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
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
            return;
        }
        Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
        if (ease == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: triggerAnimation. Must be an easing type. Example: \"LINEAR\"");
            return;
        }
        int easingID = ((Ease) ease).getId();
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);
        AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(getServerPlayer().getUUID());
        if (canPlay(aN, clientPlayer)) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), data);
        }
    }

    // Stop an animation for a specific player
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

    @Info(value = """
            Used to trigger animations on both server/client on player tick.
                        
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
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(getServerPlayer().getUUID());
        if (canPlay(aN, clientPlayer)) {
            PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
        }
    }

    private void processAnimationForAll(Object animationName, BiConsumer<Player, ResourceLocation> action) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
            return;
        }
        ResourceLocation aN = (ResourceLocation) animName;
        getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player != null) {
                AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(player.getUUID());
                //ConsoleJS.SERVER.info("[AnimationJS]: " + player + " is playing animation: " + animName + ". CanPlay: " + canPlay(aN, clientPlayer));
                if (canPlay(aN, clientPlayer)) {
                    action.accept(player, aN);
                }
            }
        });
    }

    @Info(value = """
            Used to trigger animations for all players at once with customizable animation data.
                        
            Example Usage:
            ```javascript
            event.startAnimationForAll("animationjs:waving", 1, "linear", true, false);
            ```
            """, params = {
            @Param(name = "animationID", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "transitionLength", value = "int: Duration of the transition length in milliseconds"),
            @Param(name = "easeID", value = "String: ID of the easing function to use for animation easing from the {@link dev.kosmx.playerAnim.core.util.Ease} class"),
            @Param(name = "firstPersonEnabled", value = "boolean: Whether the animation should be visible in first-person view"),
            @Param(name = "important", value = "boolean: Whether the animation is important and should override other animations")
    })
    public void startAnimationForAll(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important) {
        Object ease = AnimationJSHelperClass.convertObjectToDesired(easeID, "ease");
        if (ease == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid easeID in field: triggerAnimation. Must be an easing type. Example: \"LINEAR\"");
            return;
        }
        int easingID = ((Ease) ease).getId();
        processAnimationForAll(animationID, (player, anim) -> {
            PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), anim, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);
            PlayerAnimAPI.playPlayerAnim(((ServerPlayer) player).serverLevel(), player, data);
        });
    }

    @Info(value = """
            Used to trigger animations on both server/client for every player at once
            on player tick.
                        
            Example Usage:
            ```javascript
            event.startAnimationForAll("animationjs:waving")
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
    })
    public void startAnimationForAll(Object animationName) {
        processAnimationForAll(animationName, (player, anim) -> {
            PlayerAnimAPI.playPlayerAnim(((ServerPlayer) player).serverLevel(), player, anim);
        });
    }

    @Info(value = """
            Used to trigger animations on both server/client for every player at once
            on player tick with the option to let animations overlap themselves.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving", true)
            ```
            """, params = {
            @Param(name = "animationName", value = "ResourceLocation: The name of the animation specified in the json"),
            @Param(name = "canOverlapSelf", value = "Boolean: Whether the animation can overlap itself if it's already playing")
    })
    public void startAnimationForAll(Object animationName, boolean canOverlapSelf) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = getServerPlayer().serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(getServerPlayer().getUUID());
        getServer().getPlayerList().getPlayers().forEach(player -> {
            if (canOverlapSelf) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
            } else if (canPlay(aN, clientPlayer)) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, getServerPlayer(), aN);
            }
        });
    }

    public void stopAnimationForAll(String animationName) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
            return;
        }
        ResourceLocation aN = (ResourceLocation) animName;
        getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player != null) {
                PlayerAnimAPI.stopPlayerAnim(player.serverLevel(), player, aN);
            }
        });
    }


    private boolean canPlay(ResourceLocation aN, AbstractClientPlayer player) {
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

    private IAnimation animatorJS$getAnim(AbstractClientPlayer player) {
        if (player == null) return null;
        ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(new ResourceLocation("liosplayeranimatorapi", "factory"));
        if (anim != null) {
            return anim.getAnimation();
        } else return null;
    }


    private boolean isAnimActive(AbstractClientPlayer player) {
        return animatorJS$getAnim(player) != null && animatorJS$getAnim(player).isActive();
    }
}

