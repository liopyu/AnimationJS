package net.liopyu.animationjs.events;

import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends SimplePlayerEventJS {
    public transient Consumer<ContextUtils.PlayerRenderContext> render;

    public PlayerRenderer(Player player) {
        super(player);
    }

    public void render(Consumer<ContextUtils.PlayerRenderContext> c) {
        this.render = c;
    }

}