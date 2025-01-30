package net.liopyu.animationjs.network.server;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationStateTracker {
    private static final Map<UUID, Boolean> animationStates = new HashMap<>();

    public static void setAnimationState(ServerPlayer player, boolean isActive) {
        animationStates.put(player.getUUID(), isActive);
    }

    public static boolean getAnimationState(UUID playerUUID) {
        return animationStates.getOrDefault(playerUUID, false);
    }

    public static void removePlayer(ServerPlayer player) {
        animationStates.remove(player.getUUID());
    }
}
