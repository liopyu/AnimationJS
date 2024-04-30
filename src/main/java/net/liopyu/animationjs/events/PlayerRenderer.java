package net.liopyu.animationjs.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends SimplePlayerEventJS {
    public transient Consumer<ContextUtils.PlayerRenderContext> render;
    public transient ContextUtils.PlayerRenderContext playerRenderContext;

    public PlayerRenderer(Player player) {
        super(player);
    }

    @Info(value = """
            Used to customize rendering of player entities.
                        
            Example Usage:
            ```javascript
            event.render(context => {
            	const { renderer, entity, entityYaw, partialTicks, poseStack, buffer, packedLight } = context;
            	// Your custom rendering logic goes here
            });
            ```
            """)
    public void render(Consumer<ContextUtils.PlayerRenderContext> c) {
        this.render = c;
    }


}
