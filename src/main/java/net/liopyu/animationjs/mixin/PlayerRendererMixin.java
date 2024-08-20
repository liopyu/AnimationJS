package net.liopyu.animationjs.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Unique
    private Object animatorJS$renderer = this;

    @Unique
    private PlayerRenderer animatorJS$getRenderer() {
        return (PlayerRenderer) animatorJS$renderer;
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true, remap = true)
    public void render(AbstractClientPlayer pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        net.liopyu.animationjs.events.PlayerRenderer renderer = ClientEventHandlers.thisRenderList.get(pEntity.getUUID());
        if (renderer == null) {
            renderer = new net.liopyu.animationjs.events.PlayerRenderer(pEntity);
            ClientEventHandlers.thisRenderList.put(pEntity.getUUID(), renderer);
        }
        renderer.eventCancelled = false;
        if (EventHandlers.playerRenderer.hasListeners()) {
            renderer.playerRenderContext = new ContextUtils.PlayerRenderContext(animatorJS$getRenderer(), pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
            EventHandlers.playerRenderer.post(/*ScriptTypeHolder<ScriptTypePredicate.STARTUP_OR_CLIENT>, */renderer);
        }
        if (renderer.eventCancelled) {
            ci.cancel();
        }
    }
}