package net.liopyu.animationjs.events;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import lio.playeranimatorapi.data.PlayerParts;

import java.util.List;
import java.util.function.Consumer;

@RemapPrefixForJS("animatorJS$")
public interface IAnimationTrigger {
    void animatorJS$triggerAnimation(Object animationName);

    void animatorJS$triggerAnimation(Object animationName, boolean canOverlapSelf);

    void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important);

    void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important, List<?> modifiers, Consumer<PlayerParts> partsConsumer);

    void animatorJS$stopAnimation(Object animationName);

    boolean animatorJS$isMoving();

    @RemapForJS("isPlayingAnimation")
    boolean animatorJS$isAnimActive();
}
