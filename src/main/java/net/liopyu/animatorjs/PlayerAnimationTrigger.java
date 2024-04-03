package net.liopyu.animatorjs;

import dev.kosmx.playerAnim.api.layered.AnimationContainer;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
import io.github.kosmx.emotes.server.network.AbstractServerEmotePlay;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;


public class PlayerAnimationTrigger {
    public static void triggerAnimation(Player player, String animationName) {
        Optional<KeyframeAnimation> emote = AbstractServerEmotePlay.getLoadedEmotes().entrySet().stream().filter(it -> it.getValue().extraData.get("name").equals(animationName)).findFirst().map(Map.Entry::getValue);
        if (emote.isPresent()) {
            var e = emote.get();
            ServerEmoteAPI.setPlayerPlayingEmote(player.getUUID(), e);
        }
    }
}
