package net.liopyu.animationjs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.liopyu.animationjs.utils.AnimationJSEvents;

public class AnimationJSPlugin extends KubeJSPlugin {
    public void initStartup() {

    }

    @Override
    public void registerEvents() {
        AnimationJSEvents.AnimationJSEvents.register();
    }


    @Override
    public void registerBindings(BindingsEvent event) {
    }
}
