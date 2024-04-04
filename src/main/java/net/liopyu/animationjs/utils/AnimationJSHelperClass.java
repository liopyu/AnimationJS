package net.liopyu.animationjs.utils;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class AnimationJSHelperClass {
    public static final Set<String> errorMessagesLogged = new HashSet<>();
    public static final Set<String> warningMessagesLogged = new HashSet<>();

    public static void logErrorMessageOnce(String errorMessage) {
        if (!errorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.error(errorMessage);
            errorMessagesLogged.add(errorMessage);
        }
    }

    public static void logWarningMessageOnce(String errorMessage) {
        if (!warningMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.warn(errorMessage);
            warningMessagesLogged.add(errorMessage);
        }
    }

    public static void logErrorMessageOnceCatchable(String errorMessage, Throwable e) {
        if (!errorMessagesLogged.contains(errorMessage)) {
            ConsoleJS.CLIENT.error(errorMessage, e);
            errorMessagesLogged.add(errorMessage);
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
}
