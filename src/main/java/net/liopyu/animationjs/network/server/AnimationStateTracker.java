package net.liopyu.animationjs.network.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationStateTracker {
    private static final Map<UUID, Boolean> animationStates = new ConcurrentHashMap<>();

    public static void setAnimationState(UUID playerUUID, boolean isActive) {
        animationStates.put(playerUUID, isActive);
    }

    public static boolean getAnimationState(UUID playerUUID) {
        return animationStates.getOrDefault(playerUUID, false);
    }

    public static void clearAnimationState(UUID playerUUID) {
        animationStates.remove(playerUUID);
    }
}
