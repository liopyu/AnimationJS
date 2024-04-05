package net.liopyu.animationjs.utils;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AnimationJSHelperClass {
    public static final Set<String> clientErrorMessagesLogged = new HashSet<>();
    public static final Set<String> clientWarningMessagesLogged = new HashSet<>();
    public static final Set<String> serverErrorMessagesLogged = new HashSet<>();
    public static final Set<String> serverWarningMessagesLogged = new HashSet<>();

    public static void logServerErrorMessageOnce(String errorMessage) {
        if (!serverErrorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.SERVER.error(errorMessage);
            serverErrorMessagesLogged.add(errorMessage);
        }
    }

    public static void logClientErrorMessageOnce(String errorMessage) {
        if (!clientErrorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.error(errorMessage);
            clientErrorMessagesLogged.add(errorMessage);
        }
    }

    public static void logServerWarningMessageOnce(String errorMessage) {
        if (!serverWarningMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.warn(errorMessage);
            serverWarningMessagesLogged.add(errorMessage);
        }
    }

    public static void logClientWarningMessageOnce(String errorMessage) {
        if (!clientWarningMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.warn(errorMessage);
            clientWarningMessagesLogged.add(errorMessage);
        }
    }

    public static void logServerErrorMessageOnceCatchable(String errorMessage, Throwable e) {
        if (!serverErrorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.error(errorMessage, e);
            serverErrorMessagesLogged.add(errorMessage);
        }
    }

    public static void logClientErrorMessageOnceCatchable(String errorMessage, Throwable e) {
        if (!clientErrorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.error(errorMessage, e);
            clientErrorMessagesLogged.add(errorMessage);
        }
    }

    public static Object convertObjectToDesired(Object input, String outputType) {
        return switch (outputType.toLowerCase()) {
            case "resourcelocation" -> convertToResourceLocation(input);
            default -> input;
        };
    }

    private static ResourceLocation convertToResourceLocation(Object input) {
        if (input instanceof ResourceLocation) {
            return (ResourceLocation) input;
        } else if (input instanceof String) {
            return new ResourceLocation((String) input);
        }
        return null;
    }

    public static AbstractClientPlayer getClientPlayerByUUID(UUID playerUUID) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        ClientLevel world = minecraft.level;
        for (AbstractClientPlayer player : world.players()) {
            if (player.getUUID().equals(playerUUID)) {
                return player;
            }
        }
        return null;
    }
}
