package net.liopyu.animationjs.mixin;


import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.PlayerModelEvent;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"), remap = true)
    private void rotateBodyPart(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Player player) {
            PlayerModel<T> animatorJS$playerModel = (PlayerModel<T>) (Object) this;
            var context = new ContextUtils.PlayerSetupAnimContext<>(animatorJS$playerModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            ClientEventHandlers.thisModelList.get(entity.getUUID());
            PlayerModelEvent modelEvent = new PlayerModelEvent(player, context);
            if (EventHandlers.playerModel.hasListeners()) {
                EventHandlers.playerModel.post(modelEvent);
            }
        }
    }
}
