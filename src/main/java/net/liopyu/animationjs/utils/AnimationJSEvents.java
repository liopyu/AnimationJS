package net.liopyu.animationjs.utils;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import net.liopyu.animationjs.events.AddControllerEvent;

public class AnimationJSEvents {
    public static final EventGroup AnimationJSEvents = EventGroup.of("AnimationJSEvents");
    public static final EventHandler addController = AnimationJSEvents.startup("addController", () -> AddControllerEvent.class);

}
