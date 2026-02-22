package net.liopyu.animationjs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
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
    private void animationJS$renderArmWithItem(AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch, InteractionHand pHand, float pSwingProgress, ItemStack pStack, float pEquippedProgress, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, CallbackInfo ci) {
        var context = new ContextUtils.RenderHandsWithItemsContext(pPartialTicks, pPoseStack, (MultiBufferSource.BufferSource) pBuffer, (LocalPlayer) pPlayer, pCombinedLight, animatorJS$itemInHandRenderer, pHand);
        HandRenderEvent modelEvent = new HandRenderEvent(context);
        if (EventHandlers.handRenderer.hasListeners()) {
            if (!EventHandlers.handRenderer.post(modelEvent).pass()) {
                ci.cancel();
            }
        }
    }
}
