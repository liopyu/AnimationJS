package net.liopyu.animationjs.events.subevents.client;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lio.playeranimatorapi.events.ClientPlayerTickEvent;
import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.events.PlayerModelEvent;
import net.liopyu.animationjs.events.PlayerRenderer;
import net.liopyu.animationjs.network.NetworkHandler;
import net.liopyu.animationjs.network.packet.AnimationStateUpdatePacket;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.liopyu.animationjs.AnimationJS.MODID;

@Mod.EventBusSubscriber(modid = AnimationJS.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandlers {

    public static final Map<UUID, PlayerRenderer> thisRenderList = new HashMap<>();
    private static final ResourceLocation VISION_OVERLAY_TEXTURE = new ResourceLocation("minecraft:textures/misc/pumpkinblur.png");


    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof AbstractClientPlayer clientPlayer) {
            ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(new ResourceLocation("liosplayeranimatorapi", "factory"));
            if (anim == null) {
                return;
            }
            boolean isAnimationActive = anim.isActive();
            AnimationStateUpdatePacket packet = new AnimationStateUpdatePacket(clientPlayer.getUUID(), isAnimationActive);
            NetworkHandler.sendToServer(packet);
        }

    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(MODID, "animation"),
                42,
                ClientEventHandlers::registerPlayerAnimation);
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

    /*@SubscribeEvent
    public static void onRenderLevel(ScreenEvent.Render event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var player = (mc.player);
        var poseStack = event.getGuiGraphics().pose();
        var partialTicks = event.getPartialTick();
        //PoseStack poseStack = new PoseStack(); // Create a new PoseStack for custom rendering

        RenderSystem.disableDepthTest()

        // Bind the vision overlay texture
        RenderSystem.setShaderTexture(0, VISION_OVERLAY_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Calculate dimensions and position relative to player's head
        float scale = 0.5f; // Adjust scale if needed
        int width = 10; // Adjust width as per your texture size
        int height = 10; // Adjust height as per your texture size

        // Get player's yaw (horizontal rotation) and pitch (vertical rotation)
        float yaw = player.yRotO + (player.yRot - player.yRotO) * partialTicks;
        float pitch = player.xRotO + (player.xRot - player.xRotO) * partialTicks;

        // Calculate look vector using trigonometry
        double lookX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double lookY = -Math.sin(Math.toRadians(pitch));
        double lookZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

        // Calculate the effective distance based on the pitch to maintain consistent size
        float effectiveDistance = 1.0f / (float) Math.cos(Math.toRadians(pitch));

        // Translate to the position in front of the player
        poseStack.pushPose();
        poseStack.translate(lookX, lookY, lookZ);

        // Apply rotations to align the overlay with the player's view direction
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.scale(scale, scale, scale);

        // Calculate position centered on player's view
        float x = -width / 2.0f;
        float y = -height / 2.0f;
        float z = 0.0f; // Adjust z offset as needed

        // Render the textured rectangle using your innerBlit method
        event.getGuiGraphics().blit(VISION_OVERLAY_TEXTURE, AnimationJSHelperClass.convertToInteger(x), AnimationJSHelperClass.convertToInteger(x + width), AnimationJSHelperClass.convertToInteger(y), AnimationJSHelperClass.convertToInteger(y + height), AnimationJSHelperClass.convertToInteger(z), 0);

        // Clean up after rendering
        poseStack.popPose();

        RenderSystem.enableDepthTest();
        //renderVisionOverlay(Minecraft.getInstance().player, event.getGuiGraphics().pose(), event.getPartialTick());
    }*/

    private static void renderVisionOverlay(Player player, PoseStack poseStack, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        //PoseStack poseStack = new PoseStack(); // Create a new PoseStack for custom rendering

        RenderSystem.disableDepthTest();

        // Bind the vision overlay texture
        RenderSystem.setShaderTexture(0, VISION_OVERLAY_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Calculate dimensions and position relative to player's head
        float scale = 0.5f; // Adjust scale if needed
        int width = 10; // Adjust width as per your texture size
        int height = 10; // Adjust height as per your texture size

        // Get player's yaw (horizontal rotation) and pitch (vertical rotation)
        float yaw = player.yRotO + (player.yRot - player.yRotO) * partialTicks;
        float pitch = player.xRotO + (player.xRot - player.xRotO) * partialTicks;

        // Calculate look vector using trigonometry
        double lookX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double lookY = -Math.sin(Math.toRadians(pitch));
        double lookZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

        // Calculate the effective distance based on the pitch to maintain consistent size
        float effectiveDistance = 1.0f / (float) Math.cos(Math.toRadians(pitch));

        // Translate to the position in front of the player
        poseStack.pushPose();
        poseStack.translate(lookX, lookY, lookZ);

        // Apply rotations to align the overlay with the player's view direction
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.scale(scale, scale, scale);

        // Calculate position centered on player's view
        float x = -width / 2.0f;
        float y = -height / 2.0f;
        float z = 0.0f; // Adjust z offset as needed

        // Render the textured rectangle using your innerBlit method
        innerBlit(poseStack, VISION_OVERLAY_TEXTURE, AnimationJSHelperClass.convertToInteger(x), AnimationJSHelperClass.convertToInteger(x + width), AnimationJSHelperClass.convertToInteger(y), AnimationJSHelperClass.convertToInteger(y + height), AnimationJSHelperClass.convertToInteger(z), 0.0f, 1.0f, 0.0f, 1.0f);

        // Clean up after rendering
        poseStack.popPose();

        RenderSystem.enableDepthTest();
    }

    static void innerBlit(PoseStack pose, ResourceLocation pAtlasLocation, int pX1, int pX2, int pY1, int pY2, int pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float) pX1, (float) pY1, (float) pBlitOffset).uv(pMinU, pMinV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX1, (float) pY2, (float) pBlitOffset).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX2, (float) pY2, (float) pBlitOffset).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX2, (float) pY1, (float) pBlitOffset).uv(pMaxU, pMinV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
