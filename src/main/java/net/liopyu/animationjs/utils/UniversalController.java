package net.liopyu.animationjs.utils;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lio.playeranimatorapi.API.PlayerAnimAPI;
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

    // Stop an animation for a specific player
    public void stopAnimation(Object animationName, ServerPlayer serverPlayer) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
        if (animName == null) {
            AnimationJSHelperClass.logServerErrorMessageOnce("[AnimationJS]: Invalid animation name in field: stopAnimation. Must be a ResourceLocation.");
            return;
        }
        ServerLevel serverLevel = serverPlayer.serverLevel();
        ResourceLocation aN = (ResourceLocation) animName;
        PlayerAnimAPI.stopPlayerAnim(serverLevel, serverPlayer, aN);
    }

    // Start an animation for a specific player
    public void startAnimation(Object animationName, ServerPlayer serverPlayer) {
        Object animName = AnimationJSHelperClass.convertObjectToDesired(animationName, "resourcelocation");
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

    public void startAnimationForAll(Object animationName) {
        processAnimationForAll(animationName, (player, anim) -> {
            PlayerAnimAPI.playPlayerAnim(((ServerPlayer) player).serverLevel(), player, anim);
        });
    }

    public void stopAnimationForAll(String animationName) {
        processAnimationForAll(animationName, (player, anim) -> {
            PlayerAnimAPI.stopPlayerAnim(((ServerPlayer) player).serverLevel(), player, anim);
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

