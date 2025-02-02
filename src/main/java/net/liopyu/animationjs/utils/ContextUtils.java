package net.liopyu.animationjs.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kubejs.typings.Info;
import net.liopyu.animationjs.mixin.PlayerRendererMixin;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

public class ContextUtils {
    public static class PlayerRenderContext {
        public final PlayerRenderer renderer;
        public final AbstractClientPlayer entity;
        public final float entityYaw;
        public final float partialTicks;
        public final PoseStack poseStack;
        public final MultiBufferSource buffer;
        public final int packedLight;

        public PlayerRenderContext(PlayerRenderer renderer, AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            this.renderer = renderer;
            this.entity = entity;
            this.entityYaw = entityYaw;
            this.partialTicks = partialTicks;
            this.poseStack = poseStack;
            this.buffer = buffer;
            this.packedLight = packedLight;
        }
    }

    public static class PlayerSetupAnimContext<T extends LivingEntity> {
        public final PlayerModel<T> playerModel;
        public final T entity;
        public final float limbSwing;
        public final float limbSwingAmount;
        public final float ageInTicks;
        public final float netHeadYaw;
        public final float headPitch;

        public PlayerSetupAnimContext(PlayerModel<T> playerModel, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.playerModel = playerModel;
            this.entity = entity;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.netHeadYaw = netHeadYaw;
            this.headPitch = headPitch;
        }
    }

}
