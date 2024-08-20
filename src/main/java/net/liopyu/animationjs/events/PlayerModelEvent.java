package net.liopyu.animationjs.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.liopyu.animationjs.events.subevents.client.ClientEventHandlers;
import net.liopyu.animationjs.utils.AnimationJSHelperClass;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PlayerModelEvent extends SimplePlayerEventJS {

    public static ModelPart head;
    public static ModelPart hat;
    public static ModelPart body;
    public static ModelPart rightArm;
    public static ModelPart leftArm;
    public static ModelPart rightLeg;
    public static ModelPart leftLeg;
    public static List<ModelPart> modelParts = new ArrayList<>();
    public transient ContextUtils.PlayerSetupAnimContext<?> playerModelContext;

    public PlayerModelEvent(Player player) {
        super(player);
    }

    @Override
    public Player getEntity() {
        return (Player) playerModelContext.entity;
    }

    @Override
    public @Nullable Player getPlayer() {
        return (Player) playerModelContext.entity;
    }

    @Info(value = """
            Retrieves a list of model parts.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let parts = event.getModelParts();
            })
            ```
            """)
    public Iterable<ModelPart> getModelParts() {
        return modelParts;
    }


    @Info(value = """
            Retrieves the body model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let body = event.getBody();
            })
            ```
            """)
    public ModelPart getBody() {
        return body;
    }

    @Info(value = """
            Retrieves the head model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let head = event.getHead();
            })
            ```
            """)
    public ModelPart getHead() {
        return head;
    }

    @Info(value = """
            Retrieves the right arm model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let rightArm = event.getRightArm();
            })
            ```
            """)
    public ModelPart getRightArm() {
        return rightArm;
    }

    @Info(value = """
            Retrieves the left arm model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let leftArm = event.getLeftArm();
            })
            ```
            """)
    public ModelPart getLeftArm() {
        return leftArm;
    }

    @Info(value = """
            Retrieves the right leg model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let rightLeg = event.getRightLeg();
            })
            ```
            """)
    public ModelPart getRightLeg() {
        return rightLeg;
    }

    @Info(value = """
            Retrieves the left leg model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let leftLeg = event.getLeftLeg();
            })
            ```
            """)
    public ModelPart getLeftLeg() {
        return leftLeg;
    }

    @Info(value = """
            Retrieves the hat model part.

            Example Usage:
            ```javascript
            AnimationJS.playerRenderer(event => {
                let hat = event.getHat();
            })
            ```
            """)
    public ModelPart getHat() {
        return hat;
    }


    @Info(value = """
            Provides access to the player's model and animation parameters during the setup animation phase.
                        
            Example Usage:
            ```javascript
            AnimationJS.playerModel(event => {
            	const { playerModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch } = event.modelContext;
            })
            ```
            """)
    public ContextUtils.PlayerSetupAnimContext<?> getModelContext() {
        return playerModelContext;
    }


}
