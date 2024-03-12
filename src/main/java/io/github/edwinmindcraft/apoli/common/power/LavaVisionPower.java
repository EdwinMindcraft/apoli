package io.github.edwinmindcraft.apoli.common.power;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.AttributeModifyingPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.LavaVisionConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LavaVisionPower extends AttributeModifyingPowerFactory<LavaVisionConfiguration> {
	public static Optional<Float> getS(Entity entity) {
		return PowerContainer.getPowers(entity, ApoliPowers.LAVA_VISION.get()).stream().map(Holder::value).map(ConfiguredPower::getConfiguration).map(LavaVisionConfiguration::s).findFirst();
	}

	public static Optional<Float> getV(Entity entity) {
		return PowerContainer.getPowers(entity, ApoliPowers.LAVA_VISION.get()).stream().map(Holder::value).map(ConfiguredPower::getConfiguration).map(LavaVisionConfiguration::v).findFirst();
	}

	public LavaVisionPower() {
		super(LavaVisionConfiguration.CODEC);
	}

	@Override
	public @Nullable Attribute getAttribute() {
		return AdditionalEntityAttributes.LAVA_VISIBILITY;
	}
}
