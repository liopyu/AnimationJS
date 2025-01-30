package net.liopyu.animationjs.utils;


import com.zigythebird.playeranimatorapi.API.PlayerAnimAPIClient;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.modifier.CommonModifier;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

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
            /*case "ease" -> easeFromString(input);
            case "modifierlist" -> modifierList(input);*/
            case "integer" -> convertToInteger(input);
            case "double" -> convertToDouble(input);
            case "float" -> convertToFloat(input);
            case "itemstack" -> getStack(input);
            default -> input;
        };
    }


    public static ItemStack getStack(Object input) {
        if (input instanceof String) {
            return Objects.requireNonNull(BuiltInRegistries.ITEM.get(ResourceLocation.parse((String) input))).getDefaultInstance();
        } else if (input instanceof ResourceLocation) {
            return Objects.requireNonNull(BuiltInRegistries.ITEM.get((ResourceLocation) input)).getDefaultInstance();
        } else if (input instanceof ItemStack) {
            return (ItemStack) input;
        } else if (input instanceof Item item) {
            return item.getDefaultInstance();
        } else {
            return null;
        }
    }

    public static Integer convertToInteger(Object input) {
        if (input instanceof Integer) {
            return (Integer) input;
        } else if (input instanceof Double || input instanceof Float) {
            return ((Number) input).intValue();
        } else {
            return null;
        }
    }

    public static Double convertToDouble(Object input) {
        if (input instanceof Double) {
            return (Double) input;
        } else if (input instanceof Integer || input instanceof Float) {
            return ((Number) input).doubleValue();
        } else {
            return null;
        }
    }

    public static Float convertToFloat(Object input) {
        if (input instanceof Float) {
            return (Float) input;
        } else if (input instanceof Integer || input instanceof Double) {
            return ((Number) input).floatValue();
        } else {
            return null;
        }
    }

    // Method to convert a string representation of easing function name to Ease enum
    public static Ease easeFromString(Object functionName) {
        if (functionName instanceof String s) {
            return switch (s.toUpperCase()) {
                case "LINEAR" -> Ease.LINEAR;
                case "CONSTANT" -> Ease.CONSTANT;
                case "INSINE" -> Ease.INSINE;
                case "OUTSINE" -> Ease.OUTSINE;
                case "INOUTSINE" -> Ease.INOUTSINE;
                case "INCUBIC" -> Ease.INCUBIC;
                case "OUTCUBIC" -> Ease.OUTCUBIC;
                case "INOUTCUBIC" -> Ease.INOUTCUBIC;
                case "INQUAD" -> Ease.INQUAD;
                case "OUTQUAD" -> Ease.OUTQUAD;
                case "INOUTQUAD" -> Ease.INOUTQUAD;
                case "INQUART" -> Ease.INQUART;
                case "OUTQUART" -> Ease.OUTQUART;
                case "INOUTQUART" -> Ease.INOUTQUART;
                case "INQUINT" -> Ease.INQUINT;
                case "OUTQUINT" -> Ease.OUTQUINT;
                case "INOUTQUINT" -> Ease.INOUTQUINT;
                case "INEXPO" -> Ease.INEXPO;
                case "OUTEXPO" -> Ease.OUTEXPO;
                case "INOUTEXPO" -> Ease.INOUTEXPO;
                case "INCIRC" -> Ease.INCIRC;
                case "OUTCIRC" -> Ease.OUTCIRC;
                case "INOUTCIRC" -> Ease.INOUTCIRC;
                case "INBACK" -> Ease.INBACK;
                case "OUTBACK" -> Ease.OUTBACK;
                case "INOUTBACK" -> Ease.INOUTBACK;
                case "INELASTIC" -> Ease.INELASTIC;
                case "OUTELASTIC" -> Ease.OUTELASTIC;
                case "INOUTELASTIC" -> Ease.INOUTELASTIC;
                case "INBOUNCE" -> Ease.INBOUNCE;
                case "OUTBOUNCE" -> Ease.OUTBOUNCE;
                case "INOUTBOUNCE" -> Ease.INOUTBOUNCE;
                default -> {
                    ConsoleJS.SERVER.error("[AnimationJS]: Unknown Easing type, defaulting to \"LINEAR\"");
                    yield Ease.LINEAR;
                }
            };
        } else if (functionName instanceof Ease e) {
            return e;
        }
        return null;
    }

    public static List<CommonModifier> modifierList(Object input) {
        if (input instanceof List<?> array) {
            List<CommonModifier> list = new ArrayList<>();
            for (Object obj : array) {
                if (obj instanceof String string) {
                    list.add(new CommonModifier(ResourceLocation.parse(string), null));
                }
            }
            return list;
        }
        return null;
    }

    private static ResourceLocation convertToResourceLocation(Object input) {
        if (input instanceof ResourceLocation) {
            return (ResourceLocation) input;
        } else if (input instanceof String) {
            return ResourceLocation.parse((String) input);
        }
        return null;
    }

    public static AbstractClientPlayer getClientPlayerByUUID(UUID playerUUID) {
        if (FMLEnvironment.dist.isDedicatedServer()) return null;
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

    public static Player getPlayerByUUID(UUID playerUUID) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerList().getPlayer(playerUUID);
    }

    public static ServerPlayer getServerPlayerByUUID(UUID playerUUID) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerList().getPlayer(playerUUID);
    }

    public static ModifierLayer<IAnimation> getanimation(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> anim = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(ResourceLocation.fromNamespaceAndPath("zigysplayeranimatorapi", "factory"));
        return anim;
    }

    public static void playClientAnimation(AbstractClientPlayer player, PlayerAnimationData data) {
        PlayerAnimAPIClient.playPlayerAnim(player, data);
    }

    public static void playClientAnimation(AbstractClientPlayer player, ResourceLocation rl) {
        PlayerAnimAPIClient.playPlayerAnim(player, rl);
    }

    public static boolean isClientPlayer(Object player) {
        return player instanceof AbstractClientPlayer;
    }

}
