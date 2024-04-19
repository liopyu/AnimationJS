package net.liopyu.animationjs.utils;

import dev.kosmx.playerAnim.core.util.Ease;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import lio.playeranimatorapi.data.PlayerParts;
import lio.playeranimatorapi.modifier.CommonModifier;
import net.liopyu.animationjs.mixin.PlayerAnimationJSMixin;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;

@RemapPrefixForJS("animatorJS$")
public interface IAnimationTrigger {
    void animatorJS$triggerAnimation(Object animationName);

    void animatorJS$triggerAnimation(Object animationName, boolean canOverlapSelf);

    void animatorJS$triggerAnimation(Object animationID, int transitionLength, String easeID, boolean firstPersonEnabled, boolean important);

    void animatorJS$addAnimationController(Consumer<Animations<Player>> consumer);

    boolean animatorJS$isMoving();
}
