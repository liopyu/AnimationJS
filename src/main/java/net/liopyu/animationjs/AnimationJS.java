package net.liopyu.animationjs;

import net.liopyu.animationjs.network.packet.AnimationPacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(AnimationJS.MODID)
public class AnimationJS {
    public static final String MODID = "animationjs";
    public static final String CHANNEL_NAME = "animationjs:main";
    public static SimpleChannel CHANNEL;

    public AnimationJS() {
        registerPacketHandlers();
    }

    public static void registerPacketHandlers() {
        CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(CHANNEL_NAME), () -> "1.0", s -> true, s -> true);
        CHANNEL.registerMessage(0, AnimationPacketHandler.Message.class, AnimationPacketHandler.Message::encode, AnimationPacketHandler.Message::decode, AnimationPacketHandler::handle);
    }
}
