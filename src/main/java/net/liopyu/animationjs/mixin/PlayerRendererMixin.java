package net.liopyu.animationjs.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lio.playeranimatorapi.data.PlayerParts;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.subevents.IPlayerRenderer;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin implements IPlayerRenderer {
    @Unique
    private Object animatorJS$renderer = this;

    @Unique
    private PlayerRenderer animatorJS$getRenderer() {
        return (PlayerRenderer) animatorJS$renderer;
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    public void render(AbstractClientPlayer pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        net.liopyu.animationjs.events.PlayerRenderer renderer = ClientEventHandlers.thisRenderList.get(pEntity.getUUID());
        if (renderer == null) {
            renderer = new net.liopyu.animationjs.events.PlayerRenderer(pEntity);
            ClientEventHandlers.thisRenderList.put(pEntity.getUUID(), renderer);
        }
        if (EventHandlers.playerRenderer.hasListeners()) {
            EventHandlers.playerRenderer.post(renderer);
        }
        if (renderer.render != null) {
            ContextUtils.PlayerRenderContext context = new ContextUtils.PlayerRenderContext(animatorJS$getRenderer(), pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
            renderer.playerRenderContext = context;
            try {
                renderer.render.accept(context);
            } catch (Exception e) {
                AnimationJSHelperClass.logClientErrorMessageOnceCatchable("[AnimationJS]: Error in playerRenderer for field: render.", e);
            }
        }
    }

    @Unique
    public void animatorJS$renderBodyItem(Object itemStack, ContextUtils.PlayerRenderContext context, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation) {
        Object obj = AnimationJSHelperClass.convertObjectToDesired(itemStack, "itemstack");
        if (obj != null) {
            PoseStack poseStack = context.poseStack;
            MultiBufferSource buffer = context.buffer;
            int packedLight = context.packedLight;
            AbstractClientPlayer player = context.entity;
            float yRotationOffset = 90.0F - player.yBodyRotO + yRotation;
            float boxyRotx = animatorJS$getRenderer().getModel().body.xRot;
            float boxyRoty = animatorJS$getRenderer().getModel().body.yRot;
            float boxyRotz = animatorJS$getRenderer().getModel().body.zRot;
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
}
