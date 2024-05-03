package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.OverrideHudTexturePower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ForgeGui.class)
public abstract class InGameHudMixin extends Gui {
	public InGameHudMixin(Minecraft minecraft, ItemRenderer itemRenderer) {
		super(minecraft, itemRenderer);
	}

	@ModifyVariable(method = "bind", at = @At("HEAD"), remap = false, argsOnly = true)
	public ResourceLocation changeStatusBarTextures(ResourceLocation original) {
		if (Gui.GUI_ICONS_LOCATION.equals(original)) {
			Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
			if (first.isPresent()) {
				return first.get().value().getConfiguration().value().orElse(original);
			}
		}
		return original;
	}
}
