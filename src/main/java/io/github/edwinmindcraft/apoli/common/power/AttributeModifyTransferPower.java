package io.github.edwinmindcraft.apoli.common.power;

import com.google.common.collect.ImmutableList;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.AttributeModifyTransferConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttributeModifyTransferPower extends PowerFactory<AttributeModifyTransferConfiguration> {
	public static List<AttributeModifier> apply(Entity entity, PowerFactory<?> power) {
		return IPowerContainer.getPowers(entity, ApoliPowers.ATTRIBUTE_MODIFY_TRANSFER.get()).stream().flatMap(cp -> cp.getFactory().apply(cp, entity, power).stream()).collect(Collectors.toList());
	}

	public AttributeModifyTransferPower() {
		super(AttributeModifyTransferConfiguration.CODEC);
	}

	public List<AttributeModifier> apply(ConfiguredPower<AttributeModifyTransferConfiguration, ?> power, Entity entity, PowerFactory<?> factory) {
		ImmutableList.Builder<AttributeModifier> builder = ImmutableList.builder();
		AttributeModifyTransferConfiguration config = power.getConfiguration();
		if (entity instanceof LivingEntity living && Objects.equals(config.target(), factory)) {
			AttributeMap attributes = living.getAttributes();
			if (attributes.hasAttribute(config.source())) {
				AttributeInstance instance = attributes.getInstance(config.source());
				if (instance != null)
					instance.getModifiers().forEach(mod -> builder.add(new AttributeModifier(mod.getId(), mod.getName(), mod.getAmount() * config.multiplier(), mod.getOperation())));
			}
		}
		return builder.build();
	}
}
