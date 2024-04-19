package net.liopyu.animationjs.utils;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.API.PlayerAnimAPIClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class UniversalController extends SimplePlayerEventJS {
    private ResourceLocation currentLocation;


    public UniversalController(Player p) {
        super(p);
    }

    // Stop an animation for a specific player
    public void stopAnimation(Object animationName, Player player) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (player instanceof AbstractClientPlayer clientPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimAPIClient.stopPlayerAnim(clientPlayer, aN);
        } else if (player instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
                return;
            }
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            PlayerAnimAPI.stopPlayerAnim(serverLevel, player, aN);
        }
    }

    // Start an animation for a specific player
    public void startAnimation(Object animationName, Player player) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (player instanceof AbstractClientPlayer clientPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            if (canPlay(aN, clientPlayer)) {
                PlayerAnimAPIClient.playPlayerAnim(clientPlayer, aN);
            }
        } else if (player instanceof ServerPlayer serverPlayer) {
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: triggerAnimation. Must be a ResourceLocation.");
                return;
            }
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ResourceLocation aN = (ResourceLocation) animName;
            AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(serverPlayer.getUUID());
            if (canPlay(aN, clientPlayer)) {
                PlayerAnimAPI.playPlayerAnim(serverLevel, serverPlayer, aN);
            }
        }
    }

    // Start an animation for all players
    public void startAnimationForAll(Object animationName) {
        getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player == null) {
                return;
            }
            Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(player.getUUID());
            if (canPlay(aN, clientPlayer)) {
                PlayerAnimAPI.playPlayerAnim(player.serverLevel(), player, aN);
            }
        });
    }

    // Stop an animation for all players
    public void stopAnimationForAll(String animationName) {
        getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player == null) {
                return;
            }
            Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
            if (animName == null) {
                AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
                return;
            }
            ResourceLocation aN = (ResourceLocation) animName;
            AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(player.getUUID());
            if (canPlay(aN, clientPlayer)) {
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

    public boolean isPlayingAnimation() {
        AbstractClientPlayer clientPlayer = AnimationJSHelperClass.getClientPlayerByUUID(this.getPlayer().getUUID());
        return isAnimActive(clientPlayer);
    }


    private boolean isAnimActive(AbstractClientPlayer player) {
        return animatorJS$getAnim(player) != null && animatorJS$getAnim(player).isActive();
    }
}

