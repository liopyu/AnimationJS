package net.liopyu.animationjs.mixin;


import com.google.common.collect.ImmutableList;
import net.liopyu.animationjs.events.EventHandlers;
import net.liopyu.animationjs.events.IPlayerModel;
import net.liopyu.animationjs.events.PlayerModelEvent;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> implements IPlayerModel {
    @Unique
    private final PlayerModel<T> animatorJS$playerModel = (PlayerModel<T>) (Object) this;

    @Unique
    public PlayerModel<T> animatorJS$getPlayerModel() {
        return animatorJS$playerModel;
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"), remap = true)
    private void rotateBodyPart(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Player player) {
            if (PlayerModelEvent.modelParts.isEmpty()) {
                PlayerModelEvent.body = animatorJS$playerModel.body;
                PlayerModelEvent.hat = animatorJS$playerModel.hat;
                PlayerModelEvent.head = animatorJS$playerModel.head;
                PlayerModelEvent.leftArm = animatorJS$playerModel.leftArm;
                PlayerModelEvent.rightArm = animatorJS$playerModel.rightArm;
                PlayerModelEvent.rightLeg = animatorJS$playerModel.rightLeg;
                PlayerModelEvent.modelParts = ImmutableList.of(
                        animatorJS$playerModel.head,
                        animatorJS$playerModel.body,
                        animatorJS$playerModel.rightArm,
                        animatorJS$playerModel.leftArm,
                        animatorJS$playerModel.rightLeg,
                        animatorJS$playerModel.leftLeg,
                        animatorJS$playerModel.hat
                );
            }
            PlayerModelEvent modelEvent = ClientEventHandlers.thisModelList.get(entity.getUUID());
            if (modelEvent == null) {
                modelEvent = new net.liopyu.animationjs.events.PlayerModelEvent(player);
                ClientEventHandlers.thisModelList.put(entity.getUUID(), modelEvent);
            }
            if (EventHandlers.playerModel.hasListeners()) {
                modelEvent.playerModelContext = new ContextUtils.PlayerSetupAnimContext<>(animatorJS$getPlayerModel(), entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                EventHandlers.playerModel.post(modelEvent);
            }
        }
    }
}
