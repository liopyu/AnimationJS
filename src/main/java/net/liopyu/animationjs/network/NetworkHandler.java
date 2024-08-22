package net.liopyu.animationjs.network;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
   /* public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AnimationJS.MODID, "channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        CHANNEL.registerMessage(0, AnimationStateUpdatePacket.class, AnimationStateUpdatePacket::encode, AnimationStateUpdatePacket::decode, AnimationStateUpdatePacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }*/
}
