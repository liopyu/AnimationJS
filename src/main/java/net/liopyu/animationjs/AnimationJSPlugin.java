package net.liopyu.animationjs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import net.liopyu.animationjs.events.EventHandlers;

public class AnimationJSPlugin extends KubeJSPlugin {
    public void initStartup() {

    }

    @Override
    public void registerEvents() {
        EventHandlers.AnimationJS.register();
    }
    /*@Override
    public void registerBindings(BindingsEvent event) {
        //event.add("PlayerAnimationTrigger", PlayerAnimationTrigger.class);
        //event.add("AbstractServerEmotePlay", AbstractServerEmotePlay.class);
    }*/
}
