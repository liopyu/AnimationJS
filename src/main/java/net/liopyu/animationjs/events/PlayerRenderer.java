package net.liopyu.animationjs.events;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Renders an item on the body of a player with customizable position and rotation.
                
            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
            	const { renderer, entity, entityYaw, partialTicks, poseStack, buffer, packedLight } = event.renderContext;
            })
            ```
            """)
    public ContextUtils.PlayerRenderContext getRenderContext() {
        return playerRenderContext;
    }

    @Info(value = """
            Used to cancel the default player renderer. Doing this will halt the default minecraft
            renderer method but will not disable AnimationJS' animation render logic
            """)
    @Override
    public Object cancel() throws EventExit {
        eventCancelled = true;
        return super.cancel();
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
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Render context is null. If you see this message something is wrong, please notify the mod author.");
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
            	event.renderBodyItem("minecraft:diamond_axe", 0, 0.5, 0.25, 180, 0, 0)
            })
            ```
            """, params = {
            @Param(name = "itemStack", value = "Object: The item stack to render (String (item ID), ResourceLocation, or ItemStack)"),
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
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Renderer is null. If you see this message something is wrong, please notify the mod author.");
            return;
        }
        if (obj != null) {
            ContextUtils.PlayerRenderContext context = renderer.playerRenderContext;
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            int packedLight = context.packedLight;
            AbstractClientPlayer player = context.entity;
            float yRotationOffset = 90.0F - player.yBodyRotO;
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(0));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotationOffset));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            poseStack.translate(xOffset, yOffset, zOffset);
            poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation));

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
            	event.renderBodyItem("minecraft:diamond_axe", 0.25, 1, 0, 0, 90, 0,15)
            })
            ```
            """, params = {
            @Param(name = "itemStack", value = "Object: The item stack to render (String (item ID), ResourceLocation, or ItemStack)"),
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
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            int pL = LightTexture.pack(packedLight, packedLight);
            AbstractClientPlayer player = context.entity;
            float yRotationOffset = 90.0F - player.yBodyRotO;
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(0));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotationOffset));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            poseStack.translate(xOffset, yOffset, zOffset);
            poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation));

            Minecraft.getInstance().getItemRenderer().renderStatic((ItemStack) obj, ItemDisplayContext.NONE, pL, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);

            poseStack.popPose();
        } else {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Error in player renderer for method: renderBodyItem. ItemStack is either null or invalid");
        }
    }

    /*@Info(value = """
            Renders an item on the body of a player with customizable position and rotation with
            item lighting overlay option and a boolean deciding if the item rotates while crawling.
                
            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
            	event.renderBodyItem("minecraft:diamond_axe", 0.25, 0.7, 0, 0, 0, 90, 15, true)
            })
            ```
            """, params = {
            @Param(name = "itemStack", value = "Object: The item stack to render (String (item ID), ResourceLocation, or ItemStack)"),
            @Param(name = "xOffset", value = "Float: The offset along the X-axis"),
            @Param(name = "yOffset", value = "Float: The offset along the Y-axis"),
            @Param(name = "zOffset", value = "Float: The offset along the Z-axis"),
            @Param(name = "xRotation", value = "Float: The rotation around the X-axis (in degrees)"),
            @Param(name = "yRotation", value = "Float: The rotation around the Y-axis (in degrees)"),
            @Param(name = "zRotation", value = "Float: The rotation around the Z-axis (in degrees)"),
            @Param(name = "packedLight", value = "int: The light level of the item's model"),
            @Param(name = "rotatesWhenCrawling", value = "Boolean: Whether the item rotate when the player is crawling")
    })
    @Unique
    public void renderBodyItem(Object itemStack, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation, int packedLight, boolean rotatesWhenCrawling) {
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
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            AbstractClientPlayer player = context.entity;
            int pL = LightTexture.pack(packedLight, packedLight);
            float yRotationOffset = 90.0F - player.yBodyRotO;
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(0));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotationOffset));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));

            poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation));
            poseStack.translate(xOffset, yOffset, zOffset);

            Minecraft.getInstance().getItemRenderer().renderStatic((ItemStack) obj, ItemDisplayContext.NONE, pL, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);
            poseStack.popPose();
        } else {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Error in player renderer for method: renderBodyItem. ItemStack is either null or invalid");
        }
    }*/
}
