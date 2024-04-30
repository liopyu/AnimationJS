package net.liopyu.animationjs.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.subevents.IPlayerRenderer;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
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
    public transient ContextUtils.PlayerRenderContext animatorJS$renderContext;
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
            final ContextUtils.PlayerRenderContext context = new ContextUtils.PlayerRenderContext(animatorJS$getRenderer(), pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
            renderer.playerRenderContext = context;
            try {
                renderer.render.accept(context);

                //renderDiamondSword(pEntity, pPoseStack, pBuffer, pPackedLight, pPartialTicks);
                //renderCustomItem(Items.DIAMOND_SWORD.getDefaultInstance(), context, 1, 1, 1, 0.5F, 0.5F, 0.5F);
            } catch (Exception e) {
                AnimationJSHelperClass.logClientErrorMessageOnceCatchable("[AnimationJS]: Error in playerRenderer for field: render.", e);
            }
        }
    }

    public void renderBodyItem(Object itemStack, float xOffset, float yOffset, float zOffset, float yRotationOffset, float xRotation, float zRotation) {

        Object obj = AnimationJSHelperClass.convertObjectToDesired(itemStack, "itemstack");
        if (obj != null) {
            renderCustomItem((ItemStack) obj, animatorJS$renderContext, xOffset, yOffset, zOffset, yRotationOffset, xRotation, zRotation);
        } else {
            AnimationJSHelperClass.logClientErrorMessageOnce("[AnimationJS]: Error in player renderer for method: renderBodyItem. ItemStack is either null or invalid");
        }
    }

    private void renderCustomItem(ItemStack itemStack, ContextUtils.PlayerRenderContext context, float xOffset, float yOffset, float zOffset, float yRotationOffset, float xRotation, float zRotation) {
        PoseStack poseStack = context.poseStack;
        MultiBufferSource buffer = context.buffer;
        int packedLight = context.packedLight;
        float partialTicks = context.partialTicks;
        AbstractClientPlayer player = context.entity;
        float playerYaw = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180.0F);
        double xTranslate = Math.cos(playerYaw) * xOffset;
        double zTranslate = Math.sin(playerYaw) * zOffset;

        float yRotation = 90.0F - player.yBodyRotO + yRotationOffset;

        poseStack.pushPose();
        poseStack.translate(xTranslate, yOffset, zTranslate);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation));

        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);

        poseStack.popPose();
    }


    public void renderDiamondSword(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {

        float playerYaw = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180.0F);
        double xOffset = Math.cos(playerYaw) * 0.3;
        double zOffset = Math.sin(playerYaw) * 0.3;

        float yRotation = 90.0F - player.yBodyRotO;
        float xRotation = 180.0F;
        float zRotation = 0.0F;

        poseStack.pushPose();
        poseStack.translate(xOffset, 0.5F, zOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zRotation));
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        Minecraft.getInstance().getItemRenderer().renderStatic(diamondSword, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);
        poseStack.popPose();
    }


}
