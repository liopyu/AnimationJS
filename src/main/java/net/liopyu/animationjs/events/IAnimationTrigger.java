package net.liopyu.animationjs.events;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

@RemapPrefixForJS("animatorJS$")
public interface IAnimationTrigger {
    void animatorJS$triggerAnimation(Object animationName);

    void animatorJS$triggerAnimation(Object animationName, boolean canOverlapSelf);

    void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important);

    boolean animatorJS$isMoving();

    @RemapForJS("isPlayingAnimation")
    boolean animatorJS$isAnimActive();
}
