package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public record AttributeConfiguration(ListConfiguration<AttributedEntityAttributeModifier> modifiers,
									 boolean updateHealth) implements IDynamicFeatureConfiguration {

	public static final MapCodec<AttributeConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ListConfiguration.mapCodec(ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, "modifier", "modifiers").forGetter(AttributeConfiguration::modifiers),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "update_health", true).forGetter(AttributeConfiguration::updateHealth)
	).apply(instance, AttributeConfiguration::new));

	public AttributeConfiguration(AttributedEntityAttributeModifier... modifiers) {
		this(ListConfiguration.of(modifiers), true);
	}

	public static final Codec<AttributeConfiguration> CODEC = MAP_CODEC.codec();


	public void add(Entity entity) {
		if (!(entity instanceof LivingEntity living) || entity.level().isClientSide())
			return;
		float previousMaxHealth = living.getMaxHealth();
		float previousHealthPercent = living.getHealth() / previousMaxHealth;
		this.modifiers().getContent().stream().filter(x -> living.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			AttributeInstance attributeInstance = living.getAttribute(mod.attribute());
			if (attributeInstance != null && !attributeInstance.hasModifier(mod.modifier()))
				attributeInstance.addTransientModifier(mod.modifier());
		});
		float afterMaxHealth = living.getMaxHealth();
		if (this.updateHealth() && afterMaxHealth != previousMaxHealth)
			living.setHealth(afterMaxHealth * previousHealthPercent);
	}

	public void remove(Entity entity) {
		if (!(entity instanceof LivingEntity living) || entity.level().isClientSide())
			return;
		float previousMaxHealth = living.getMaxHealth();
		float previousHealthPercent = living.getHealth() / previousMaxHealth;
		this.modifiers().getContent().stream().filter(x -> living.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			AttributeInstance attributeInstance = living.getAttribute(mod.attribute());
			if (attributeInstance != null && attributeInstance.hasModifier(mod.modifier()))
				attributeInstance.removeModifier(mod.modifier());
		});
		float afterMaxHealth = living.getMaxHealth();
		if (this.updateHealth() && afterMaxHealth != previousMaxHealth)
			living.setHealth(afterMaxHealth * previousHealthPercent);
	}
}
