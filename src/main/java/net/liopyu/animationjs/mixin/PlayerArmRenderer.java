package net.liopyu.animationjs.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.HandRenderEvent;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class PlayerArmRenderer {
    @Unique
    private ItemInHandRenderer animatorJS$itemInHandRenderer = (ItemInHandRenderer) (Object) this;

    @Inject(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), cancellable = true)
    private void animationJS$renderHandsWithItems(float pPartialTicks, PoseStack pPoseStack, MultiBufferSource.BufferSource pBuffer, LocalPlayer pPlayerEntity, int pCombinedLight, CallbackInfo ci) {
        var context = new ContextUtils.RenderHandsWithItemsContext(pPartialTicks, pPoseStack, pBuffer, pPlayerEntity, pCombinedLight, animatorJS$itemInHandRenderer);
        HandRenderEvent modelEvent = new HandRenderEvent(context);
        if (EventHandlers.handRenderer.hasListeners()) {
            if (!EventHandlers.handRenderer.post(modelEvent).pass()) {
                ci.cancel();
            }
        }
    }
}
