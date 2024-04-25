package net.liopyu.animationjs.events;


import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import io.netty.buffer.Unpooled;
import lio.liosmultiloaderutils.utils.NetworkManager;
import lio.playeranimatorapi.API.PlayerAnimAPI;
import lio.playeranimatorapi.ModInit;
import lio.playeranimatorapi.data.PlayerAnimationData;
import lio.playeranimatorapi.data.PlayerParts;
import lio.playeranimatorapi.geckolib.ModGeckoLibUtilsClient;
import lio.playeranimatorapi.utils.CommonPlayerLookup;
import net.liopyu.animationjs.network.server.AnimationStateTracker;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class UniversalController extends SimplePlayerEventJS {
    private static final Logger logger = LogManager.getLogger(ModInit.class);
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
            Used to trigger animations on player tick.
                        
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
        if (canPlay(aN, this.getPlayer())) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled,
                        null, -1, -1, false, false);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeUtf(PlayerAnimAPI.gson.toJson(PlayerAnimationData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow(true, logger::warn)));
                NetworkManager.sendToPlayer(player, PlayerAnimAPI.playerAnimPacket, buf);
            });
        }
    }

    @Info(value = """
            Used to trigger animations on player tick with the option
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
        if (canOverlapSelf) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled,
                        null, -1, -1, false, false);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeUtf(PlayerAnimAPI.gson.toJson(PlayerAnimationData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow(true, logger::warn)));
                NetworkManager.sendToPlayer(player, PlayerAnimAPI.playerAnimPacket, buf);
            });
        } else if (canPlay(aN, this.getPlayer())) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled,
                        null, -1, -1, false, false);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeUtf(PlayerAnimAPI.gson.toJson(PlayerAnimationData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow(true, logger::warn)));
                NetworkManager.sendToPlayer(player, PlayerAnimAPI.playerAnimPacket, buf);
            });
        }
    }

    @Info(value = """
            Used to trigger animations on player tick with customizable animation data.
                        
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
        if (canPlay(aN, this.getPlayer())) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                PlayerAnimationData data = new PlayerAnimationData(getServerPlayer().getUUID(), aN, PlayerParts.allEnabled, null, transitionLength, easingID, firstPersonEnabled, important);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeUtf(PlayerAnimAPI.gson.toJson(PlayerAnimationData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow(true, logger::warn)));
                NetworkManager.sendToPlayer(player, PlayerAnimAPI.playerAnimPacket, buf);
            });
        }
    }

    @Info(value = """
            Used to stop a certain player animation.
                        
            Example Usage:
            ```javascript
            event.startAnimation("animationjs:waving")
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
        getServer().getPlayerList().getPlayers().forEach(player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUUID(this.getPlayer().getUUID());
            buf.writeResourceLocation(aN);
            NetworkManager.sendToPlayer(player, PlayerAnimAPI.playerAnimStopPacket, buf);
        });
    }
}

