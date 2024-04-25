package net.liopyu.animationjs;

import net.liopyu.animationjs.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;

@Mod(AnimationJS.MODID)
public class AnimationJS {
    public static final String MODID = "animationjs";

    public AnimationJS() {
        NetworkHandler.init();
    }
}

