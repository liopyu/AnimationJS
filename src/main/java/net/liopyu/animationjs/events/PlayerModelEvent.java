package net.liopyu.animationjs.events;

import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@Info(value = """
        Initializes a new PlayerModelEvent with the given player and animation context. This event provides access to the player model and various animation parameters during the setup animation phase.
        Example Usage:
        ```javascript
        AnimationJS.playerModel(event => {
            const { playerModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch } = event;
            // Use the playerModel and animation parameters as needed
        });
        ```
        Parameters:
        - player: The player entity associated with this event.
        - playerModelContext: The context containing the player model and animation parameters.
        """)
@OnlyIn(Dist.CLIENT)
public class PlayerModelEvent extends SimplePlayerEventJS {
    protected final ContextUtils.PlayerSetupAnimContext<?> playerModelContext;
    public PlayerModel<?> playerModel;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;

    public PlayerModelEvent(Player player, ContextUtils.PlayerSetupAnimContext<?> playerModelContext) {
        super(player);
        this.playerModelContext = playerModelContext;
        this.playerModel = playerModelContext.playerModel;
        this.limbSwing = playerModelContext.limbSwing;
        this.limbSwingAmount = playerModelContext.limbSwingAmount;
        this.ageInTicks = playerModelContext.ageInTicks;
        this.netHeadYaw = playerModelContext.netHeadYaw;
        this.headPitch = playerModelContext.headPitch;
    }

    @Info(value = """
            Retrieves the player model associated with the current animation context.
                        
            Example Usage:
            ```javascript
            const playerModel = event.getPlayerModel();
            ```
            """)
    public PlayerModel<?> getPlayerModel() {
        return playerModel;
    }

    @Info(value = """
            Retrieves the age of the entity in ticks, used for animations that depend on the entity's lifetime.
                        
            Example Usage:
            ```javascript
            const ageInTicks = event.getAgeInTicks();
            ```
            """)
    public float getAgeInTicks() {
        return ageInTicks;
    }

    @Info(value = """
            Retrieves the pitch of the entity's head, used for controlling vertical head movement in animations.
                        
            Example Usage:
            ```javascript
            const headPitch = event.getHeadPitch();
            ```
            """)
    public float getHeadPitch() {
        return headPitch;
    }

    @Info(value = """
            Retrieves the limb swing value, which represents the movement of the entity's limbs (e.g., legs) during walking animations.
                        
            Example Usage:
            ```javascript
            const limbSwing = event.getLimbSwing();
            ```
            """)
    public float getLimbSwing() {
        return limbSwing;
    }

    @Info(value = """
            Retrieves the limb swing amount, which represents the intensity or magnitude of the limb swing during animations.
                        
            Example Usage:
            ```javascript
            const limbSwingAmount = event.getLimbSwingAmount();
            ```
            """)
    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    @Info(value = """
            Retrieves the yaw of the entity's head, used for controlling horizontal head movement in animations.
                        
            Example Usage:
            ```javascript
            const netHeadYaw = event.getNetHeadYaw();
            ```
            """)
    public float getNetHeadYaw() {
        return netHeadYaw;
    }


    @Override
    public Player getEntity() {
        return (Player) playerModelContext.entity;
    }

    @Override
    public @Nullable Player getPlayer() {
        return (Player) playerModelContext.entity;
    }
}