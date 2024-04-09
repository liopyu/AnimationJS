package net.liopyu.animationjs.network.packet;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.liopyu.animationjs.AnimationJS;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AnimationPacketHandler {
    public static void handle(Message message, Supplier<NetworkEvent.Context> contextSupplier) {
        UUID playerUUID = message.playerUUID;
        //ResourceLocation animationName = message.animationName;
        ResourceLocation animationName = new ResourceLocation(AnimationJS.MODID, "waving");
        Player player = AnimationJSHelperClass.getPlayerByUUID(playerUUID);
        ServerLevel serverLevel = (ServerLevel) ((ServerPlayer) player).level();
        try {
            //PlayerAnimAPI.playPlayerAnim(serverLevel, player, animationName);
        } catch (Throwable e) {
            ConsoleJS.SERVER.error(e.getMessage());
        }
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