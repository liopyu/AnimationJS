package net.liopyu.animatorjs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
import io.github.kosmx.emotes.server.network.AbstractServerEmotePlay;

public class AnimatorJSPlugin extends KubeJSPlugin {
    public void initStartup() {

    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("PlayerAnimationTrigger", PlayerAnimationTrigger.class);
        event.add("AbstractServerEmotePlay", AbstractServerEmotePlay.class);
    }
}
