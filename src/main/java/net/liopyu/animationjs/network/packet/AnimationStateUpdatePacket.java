package net.liopyu.animationjs.network.packet;

public class AnimationStateUpdatePacket {
   /* public final UUID playerUUID;
    public final boolean isActive;

    public AnimationStateUpdatePacket(UUID playerUUID, boolean isActive) {
        this.playerUUID = playerUUID;
        this.isActive = isActive;
    }

    public static AnimationStateUpdatePacket decode(FriendlyByteBuf buf) {
        return new AnimationStateUpdatePacket(buf.readUUID(), buf.readBoolean());
    }

    *//*public static void handle(AnimationStateUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        UUID playerUUID = packet.playerUUID;
        boolean isActive = packet.isActive;

        AnimationStateTracker.setAnimationState(playerUUID, isActive);

        context.get().setPacketHandled(true);
    }*//*


    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeBoolean(isActive);
    }*/
}