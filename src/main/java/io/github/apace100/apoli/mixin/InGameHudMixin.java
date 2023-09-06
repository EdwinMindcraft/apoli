package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.OverrideHudTexturePower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Shadow @Final protected static ResourceLocation GUI_ICONS_LOCATION;

    @Shadow @Final protected Minecraft minecraft;

    @ModifyArg(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeStatusBarTextures(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }

    @ModifyArg(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeHearts(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeXpBarTextures(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeCrosshair(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }

    @ModifyArg(method = "renderJumpMeter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeMountJumpBar(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }

    @ModifyArg(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    public ResourceLocation changeMountHealth(ResourceLocation original) {
        if (GUI_ICONS_LOCATION.equals(original)) {
            Optional<Holder<ConfiguredPower<FieldConfiguration<Optional<ResourceLocation>>, OverrideHudTexturePower>>> first = IPowerContainer.getPowers(this.minecraft.player, ApoliPowers.STATUS_BAR_TEXTURE.get()).stream().filter(Holder::isBound).findFirst();
            if (first.isPresent()) {
                return first.get().value().getConfiguration().value().orElse(null);
            }
        }
        return original;
    }
}
