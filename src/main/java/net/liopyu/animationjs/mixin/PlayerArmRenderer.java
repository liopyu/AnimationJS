package net.liopyu.animationjs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.liopyu.animationjs.events.ArmRenderEvent;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.HandRenderEvent;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemInHandRenderer.class)
public class PlayerArmRenderer {
    @Unique
    private ItemInHandRenderer animatorJS$itemInHandRenderer = (ItemInHandRenderer) (Object) this;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void animationJS$renderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (EventHandlers.handRenderer.hasListeners()) {
            var context = new ContextUtils.RenderHandsWithItemsContext(partialTicks, poseStack, (MultiBufferSource.BufferSource) buffer, (LocalPlayer) player, combinedLight, animatorJS$itemInHandRenderer, hand);
            HandRenderEvent modelEvent = new HandRenderEvent(context);
            if (!EventHandlers.handRenderer.post(modelEvent).pass()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isScoping()Z", shift = At.Shift.AFTER))
    private void animationJS$renderArmWithItemTransform(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (EventHandlers.armRenderer.hasListeners()) {
            var context = new ContextUtils.RenderHandsWithItemsContext(partialTicks, poseStack, (MultiBufferSource.BufferSource) buffer, (LocalPlayer) player, combinedLight, animatorJS$itemInHandRenderer, hand);
            ArmRenderEvent modelEvent = new ArmRenderEvent(context);
            EventHandlers.armRenderer.post(modelEvent);
        }
    }
}
