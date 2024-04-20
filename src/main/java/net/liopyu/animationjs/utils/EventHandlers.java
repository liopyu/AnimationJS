package net.liopyu.animationjs.utils;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public class EventHandlers {
    public static final EventGroup AnimationJS = EventGroup.of("AnimationJS");
    public static final EventHandler universalController = AnimationJS.server("universalController", () -> UniversalController.class);

}
