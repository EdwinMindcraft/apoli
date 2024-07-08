package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IAttributeModifyingPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class AttributeModifyingPowerFactory<T extends IAttributeModifyingPowerConfiguration> extends PowerFactory<T> {

	private final Holder<Attribute> attribute;

	protected AttributeModifyingPowerFactory(MapCodec<T> codec) {
		this(codec, true);
	}

	protected AttributeModifyingPowerFactory(MapCodec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
		this.attribute = getAttribute();
	}

	public boolean hasAttributeBacking() {
		return this.attribute != null;
	}

	private Optional<AttributeInstance> getAttribute(Entity entity) {
		if (entity instanceof LivingEntity player)
			return player.getAttributes().hasAttribute(this.attribute) ? Optional.ofNullable(player.getAttribute(attribute)) : Optional.empty();
		return Optional.empty();
	}

	protected void add(List<AttributeModifier> configuration, Entity player) {
		this.getAttribute(player).ifPresent(x -> configuration.stream().filter(mod -> !x.hasModifier(mod.id())).forEach(x::addTransientModifier));
	}

	protected void remove(List<AttributeModifier> configuration, Entity player) {
		this.getAttribute(player).ifPresent(x -> configuration.stream().filter(mod -> x.hasModifier(mod.id())).forEach(x::removeModifier));
	}

	@Override
	public boolean canTick(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.hasAttributeBacking() && this.shouldCheckConditions(configuration, entity);
	}

	@Override
	public void tick(ConfiguredPower<T, ?> configuration, Entity player) {
		if (this.canTick(configuration, player)) {
			if (configuration.isActive(player))
				this.add(configuration.getConfiguration().modifiers().getContent(), player);
			else
				this.remove(configuration.getConfiguration().modifiers().getContent(), player);
		}
	}

	@Override
	public void onAdded(ConfiguredPower<T, ?> configuration, Entity entity) {
		configuration.getConfiguration().createModifiers(configuration.getRegistryName());
		if (this.hasAttributeBacking() && !this.shouldCheckConditions(configuration, entity))
			this.add(configuration.getConfiguration().modifiers().getContent(), entity);
	}

	@Override
	protected void onRemoved(T configuration, Entity player) {
		this.remove(configuration.modifiers().getContent(), player);
	}

	@Override
	protected int tickInterval(T configuration, Entity player) {
		return 20;
	}

	@Nullable
	public abstract Holder<Attribute> getAttribute();
}
