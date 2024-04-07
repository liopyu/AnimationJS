package net.liopyu.animationjs.network.packet;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.liopyu.animationjs.PlayerAnimationTrigger;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AnimationPacketHandler {
    public static void handle(Message message, Supplier<NetworkEvent.Context> contextSupplier) {
        UUID playerUUID = message.playerUUID;
        ResourceLocation animationName = message.animationName;
        AbstractClientPlayer player = AnimationJSHelperClass.getClientPlayerByUUID(playerUUID);
        PlayerAnimationTrigger.triggerAnimationOnClient(player, animationName);
        KeyframeAnimation anim = new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(animationName)).getData();

        contextSupplier.get().setPacketHandled(true);
    }

    public static class Message {
        public final UUID playerUUID;
        public final ResourceLocation animationName;

        public Message(UUID playerUUID, ResourceLocation animationName) {
            this.playerUUID = playerUUID;
            this.animationName = animationName;
        }

        public static void encode(Message message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.playerUUID);
            buffer.writeResourceLocation(message.animationName);
        }

        public static Message decode(FriendlyByteBuf buffer) {
            UUID playerUUID = buffer.readUUID();
            ResourceLocation animationName = buffer.readResourceLocation();
            return new Message(playerUUID, animationName);
        }
    }
}