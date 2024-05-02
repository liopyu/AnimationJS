package net.liopyu.animationjs.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends SimplePlayerEventJS {
    public transient ContextUtils.PlayerRenderContext playerRenderContext;
    public transient boolean eventCancelled;

    public PlayerRenderer(Player player) {
        super(player);
        eventCancelled = false;
    }


    @Override
    public Player getEntity() {
        return playerRenderContext.entity;
    }

    @Override
    public @Nullable Player getPlayer() {
        return playerRenderContext.entity;
    }

    @Info(value = """
            Used to get the current render context.
            """)
    public ContextUtils.PlayerRenderContext getRenderContext() {
        return playerRenderContext;
    }

    @Info(value = """
            Used to cancel the default player renderer.
            """)
    public void cancelDefaultRender() {
        eventCancelled = true;
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
        if (playerRenderContext == null) {
            ConsoleJS.CLIENT.info("Renderer is null");
            return;
        }
        try {
            c.accept(playerRenderContext);
        } catch (Exception e) {
            AnimationJSHelperClass.logClientErrorMessageOnceCatchable("[AnimationJS]: Error in playerRenderer for field: render.", e);
        }
    }

    @Info(value = """
            Renders an item on the body of a player with customizable position and rotation.
                
            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
            	event.render(context => {
            		const { renderer, entity, entityYaw, partialTicks, poseStack, buffer, packedLight } = context
            		renderer.renderBodyItem("minecraft:diamond_axe", context, 0, 1, 0.25, 180, -90, 0)
            	})
            })
            ```
            """, params = {
            @Param(name = "itemStack", value = "Object: The item stack to render (String (item ID), ResourceLocation, or ItemStack)"),
            @Param(name = "context", value = "ContextUtils.PlayerRenderContext: The rendering context"),
            @Param(name = "xOffset", value = "Float: The offset along the X-axis"),
            @Param(name = "yOffset", value = "Float: The offset along the Y-axis"),
            @Param(name = "zOffset", value = "Float: The offset along the Z-axis"),
            @Param(name = "xRotation", value = "Float: The rotation around the X-axis (in degrees)"),
            @Param(name = "yRotation", value = "Float: The rotation around the Y-axis (in degrees)"),
            @Param(name = "zRotation", value = "Float: The rotation around the Z-axis (in degrees)")
    })
    @Unique
    public void renderBodyItem(Object itemStack, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation) {
        if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen ||
                Minecraft.getInstance().screen instanceof InventoryScreen) return;
        Object obj = AnimationJSHelperClass.convertObjectToDesired(itemStack, "itemstack");
        net.liopyu.animationjs.events.PlayerRenderer renderer = ClientEventHandlers.thisRenderList.get(this.getPlayer().getUUID());
        if (renderer == null) {
            return;
        }
        if (obj != null) {
            ContextUtils.PlayerRenderContext context = renderer.playerRenderContext;
            var playerRenderer = context.renderer;
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            int packedLight = context.packedLight;
            AbstractClientPlayer player = context.entity;
            float yRotationOffset = 90.0F - player.yBodyRotO + yRotation;
            float boxyRotx = playerRenderer.getModel().body.xRot;
            float boxyRoty = playerRenderer.getModel().body.yRot;
            float boxyRotz = playerRenderer.getModel().body.zRot;
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotationOffset - boxyRoty));
            poseStack.mulPose(Axis.XP.rotationDegrees(xRotation - boxyRotx));
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation - boxyRotz));
            poseStack.translate(xOffset, -yOffset, zOffset);
            Minecraft.getInstance().getItemRenderer().renderStatic((ItemStack) obj, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);

            poseStack.popPose();
        } else {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Error in player renderer for method: renderBodyItem. ItemStack is either null or invalid");
        }
    }

    @Info(value = """
            Renders an item on the body of a player with customizable position and rotation with
            item lighting overlay option.
                
            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
            	event.render(context => {
            		const { renderer, entity, entityYaw, partialTicks, poseStack, buffer, packedLight } = context
            		renderer.renderBodyItem("minecraft:diamond_axe", context, 0, 1, 0.25, 180, -90, 0, 15)
            	})
            })
            ```
            """, params = {
            @Param(name = "itemStack", value = "Object: The item stack to render (String (item ID), ResourceLocation, or ItemStack)"),
            @Param(name = "context", value = "ContextUtils.PlayerRenderContext: The rendering context"),
            @Param(name = "xOffset", value = "Float: The offset along the X-axis"),
            @Param(name = "yOffset", value = "Float: The offset along the Y-axis"),
            @Param(name = "zOffset", value = "Float: The offset along the Z-axis"),
            @Param(name = "xRotation", value = "Float: The rotation around the X-axis (in degrees)"),
            @Param(name = "yRotation", value = "Float: The rotation around the Y-axis (in degrees)"),
            @Param(name = "zRotation", value = "Float: The rotation around the Z-axis (in degrees)"),
            @Param(name = "packedLight", value = "int: The light level of the item's model")
    })
    @Unique
    public void renderBodyItem(Object itemStack, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation, int packedLight) {
        if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen ||
                Minecraft.getInstance().screen instanceof InventoryScreen) return;
        Object obj = AnimationJSHelperClass.convertObjectToDesired(itemStack, "itemstack");
        net.liopyu.animationjs.events.PlayerRenderer renderer = ClientEventHandlers.thisRenderList.get(this.getPlayer().getUUID());
        if (renderer == null) {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Renderer is null. If you see this message something is wrong, please notify the mod author.");
            return;
        }
        if (obj != null) {
            ContextUtils.PlayerRenderContext context = renderer.playerRenderContext;
            var playerRenderer = context.renderer;
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            AbstractClientPlayer player = context.entity;
            float yRotationOffset = 90.0F - player.yBodyRotO + yRotation;
            float boxyRotx = playerRenderer.getModel().body.xRot;
            float boxyRoty = playerRenderer.getModel().body.yRot;
            float boxyRotz = playerRenderer.getModel().body.zRot;

            int pL = LightTexture.pack(packedLight, packedLight);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotationOffset - boxyRoty));
            poseStack.mulPose(Axis.XP.rotationDegrees(xRotation - boxyRotx));
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation - boxyRotz));
            poseStack.translate(xOffset, -yOffset, zOffset);
            Minecraft.getInstance().getItemRenderer().renderStatic((ItemStack) obj, ItemDisplayContext.NONE, pL, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);

            poseStack.popPose();
        } else {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Error in player renderer for method: renderBodyItem. ItemStack is either null or invalid");
        }
    }
}
