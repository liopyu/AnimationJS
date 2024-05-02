package net.liopyu.animationjs.events.subevents;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.liopyu.animationjs.utils.ContextUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//@OnlyIn(Dist.CLIENT)
@RemapPrefixForJS("animatorJS$")
public interface IPlayerRenderer {
    /*void animatorJS$renderBodyItem(Object itemStack, ContextUtils.PlayerRenderContext context, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation, int packedLight);

    void animatorJS$renderBodyItem(Object itemStack, ContextUtils.PlayerRenderContext context, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation);
*/
}