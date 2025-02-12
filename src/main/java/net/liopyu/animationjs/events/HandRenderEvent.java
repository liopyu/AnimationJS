package net.liopyu.animationjs.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

@SuppressWarnings("unused")
public class HandRenderEvent extends SimplePlayerEventJS {
    public final float partialTicks;
    public final PoseStack poseStack;
    public final MultiBufferSource.BufferSource buffer;
    public final int combinedLight;
    public final ItemInHandRenderer itemInHandRenderer;

    public HandRenderEvent(ContextUtils.RenderHandsWithItemsContext context) {
        super(context.playerEntity);
        this.partialTicks = context.partialTicks;
        this.poseStack = context.poseStack;
        this.buffer = context.buffer;
        this.combinedLight = context.combinedLight;
        this.itemInHandRenderer = context.itemInHandRenderer;
    }

    @Info(value = """
            Retrieves the item-in-hand renderer, which handles rendering items held by the player.
            
            Example Usage:
            ```javascript
            const itemRenderer = event.getItemInHandRenderer();
            ```
            """)
    public ItemInHandRenderer getItemInHandRenderer() {
        return itemInHandRenderer;
    }

    @Info(value = """
            Retrieves the buffer source used for rendering vertex data.
            
            Example Usage:
            ```javascript
            const buffer = event.getBuffer();
            ```
            """)
    public MultiBufferSource.BufferSource getBuffer() {
        return buffer;
    }

    @Info(value = """
            Retrieves the partial tick value used for rendering interpolation.
            
            Example Usage:
            ```javascript
            const partialTicks = event.getPartialTicks();
            ```
            """)
    public float getPartialTicks() {
        return partialTicks;
    }

    @Info(value = """
            Retrieves the combined light value used for lighting calculations during rendering.
            
            Example Usage:
            ```javascript
            const combinedLight = event.getCombinedLight();
            ```
            """)
    public int getCombinedLight() {
        return combinedLight;
    }

    @Info(value = """
            Retrieves the current pose stack used for matrix transformations during rendering.
            
            Example Usage:
            ```javascript
            const poseStack = event.getPoseStack();
            ```
            """)
    public PoseStack getPoseStack() {
        return poseStack;
    }
}