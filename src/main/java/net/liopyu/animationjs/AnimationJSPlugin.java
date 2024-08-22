package net.liopyu.animationjs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import net.liopyu.animationjs.events.EventHandlers;

public class AnimationJSPlugin implements KubeJSPlugin {
    public void initStartup() {
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(EventHandlers.AnimationJS);
    }

    /*@Override
    public void registerBindings(BindingsEvent event) {
        //event.add("PlayerAnimationTrigger", PlayerAnimationTrigger.class);
        //event.add("AbstractServerEmotePlay", AbstractServerEmotePlay.class);
    }*/
}
