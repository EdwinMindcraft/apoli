package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ElytraFlightConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.caelus.api.CaelusApi;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ElytraFlightPower extends PowerFactory<ElytraFlightConfiguration> {
	public static final AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(UUID.fromString("29eb14ca-c803-4af6-81e2-86e9bf1d4857"), "Elytra modifier", 1.0F, AttributeModifier.Operation.ADDITION);

	public static boolean shouldRenderElytra(Entity entity) {
		return PowerContainer.getPowers(entity, ApoliPowers.ELYTRA_FLIGHT.get()).stream().anyMatch(x -> x.value().getConfiguration().render());
	}

	public static Optional<ResourceLocation> getElytraTexture(Entity entity) {
		return PowerContainer.getPowers(entity, ApoliPowers.ELYTRA_FLIGHT.get()).stream().map(x -> x.value().getConfiguration().texture()).filter(Objects::nonNull).findFirst();
	}

	public static void enableFlight(Entity entity) {
		if (!(entity instanceof LivingEntity living))
			return;
		if (living.getAttributes().hasAttribute(CaelusApi.getInstance().getFlightAttribute())) {
			AttributeInstance attributeInstance = living.getAttribute(CaelusApi.getInstance().getFlightAttribute());
			if (!attributeInstance.hasModifier(FLIGHT_MODIFIER))
				attributeInstance.addTransientModifier(FLIGHT_MODIFIER);
		}
	}

	public static void disableFlight(Entity entity) {
		if (!(entity instanceof LivingEntity living))
			return;
		if (living.getAttributes().hasAttribute(CaelusApi.getInstance().getFlightAttribute())) {
			AttributeInstance attributeInstance = living.getAttribute(CaelusApi.getInstance().getFlightAttribute());
			if (attributeInstance.hasModifier(FLIGHT_MODIFIER))
				attributeInstance.removeModifier(FLIGHT_MODIFIER);
		}
	}

	public ElytraFlightPower() {
		super(ElytraFlightConfiguration.CODEC);
		this.ticking(true);
	}

	@Override
	public void tick(ConfiguredPower<ElytraFlightConfiguration, ?> configuration, Entity player) {
		if (configuration.isActive(player))
			enableFlight(player);
		else
			disableFlight(player);
	}

	@Override
	protected void onRemoved(ElytraFlightConfiguration configuration, Entity entity) {
		disableFlight(entity);
	}
}
