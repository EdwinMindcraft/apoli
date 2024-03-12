package io.github.edwinmindcraft.apoli.common.power;

import com.google.common.collect.ImmutableList;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
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
	public static List<ConfiguredModifier<?>> apply(Entity entity, PowerFactory<?> power) {
		return PowerContainer.getPowers(entity, ApoliPowers.ATTRIBUTE_MODIFY_TRANSFER.get()).stream().flatMap(cp -> cp.value().getFactory().apply(cp.value(), entity, power).stream()).collect(Collectors.toList());
	}

	public AttributeModifyTransferPower() {
		super(AttributeModifyTransferConfiguration.CODEC);
	}

	public List<ConfiguredModifier<?>> apply(ConfiguredPower<AttributeModifyTransferConfiguration, ?> power, Entity entity, PowerFactory<?> factory) {
		ImmutableList.Builder<ConfiguredModifier<?>> builder = ImmutableList.builder();
		AttributeModifyTransferConfiguration config = power.getConfiguration();
		if (entity instanceof LivingEntity living && Objects.equals(config.target(), factory)) {
			AttributeMap attributes = living.getAttributes();
			if (attributes.hasAttribute(config.source())) {
				AttributeInstance instance = attributes.getInstance(config.source());
				if (instance != null)
					instance.getModifiers().forEach(mod -> builder.add(ModifierUtil.fromAttributeModifier(new AttributeModifier(mod.getId(), mod.getName(), mod.getAmount() * config.multiplier(), mod.getOperation()))));
			}
		}
		return builder.build();
	}
}
