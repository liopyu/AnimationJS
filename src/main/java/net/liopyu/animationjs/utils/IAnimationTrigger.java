package net.liopyu.animationjs.utils;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;

@RemapPrefixForJS("animatorJS$")
public interface IAnimationTrigger {
    void animatorJS$triggerAnimation(Object animationName);
}
