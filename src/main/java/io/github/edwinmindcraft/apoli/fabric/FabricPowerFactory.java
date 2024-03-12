package io.github.edwinmindcraft.apoli.fabric;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FabricPowerFactory<P extends Power> extends PowerFactory<FabricPowerConfiguration<P>> {
	public FabricPowerFactory(Codec<FabricPowerConfiguration<P>> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	@SuppressWarnings("unchecked")
	private P getPower(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, LivingEntity entity) {
		return configuration.getPowerData(entity, () -> configuration.getConfiguration().power().apply((PowerType<P>) configuration.getPowerType(), entity));
	}

	@SuppressWarnings("unchecked")
	private P getPower(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, LivingEntity entity, PowerContainer container) {
		return configuration.getPowerData(container, () -> configuration.getConfiguration().power().apply((PowerType<P>) configuration.getPowerType(), entity));
	}

	@Override
	public boolean isActive(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		return entity instanceof LivingEntity living && super.isActive(configuration, entity) && this.getPower(configuration, living).isActive();
	}

	@Override
	protected boolean shouldTickWhenActive(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		return entity instanceof LivingEntity living && this.getPower(configuration, living).shouldTick();
	}

	@Override
	protected boolean shouldTickWhenInactive(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		return entity instanceof LivingEntity living && this.getPower(configuration, living).shouldTickWhenInactive();
	}

	@Override
	public void tick(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).tick();
	}

	@Override
	public void onGained(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).onGained();
	}

	@Override
	public void onLost(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).onLost();
	}

	@Override
	public void onAdded(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).onAdded();
	}

	@Override
	public void onRemoved(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).onRemoved();
	}

	@Override
	public void onRespawn(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			this.getPower(configuration, living).onRespawn();
	}

	@Override
	public void serialize(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, PowerContainer container, CompoundTag tag) {
		if (container.getOwner() instanceof LivingEntity living)
			tag.put("FabricData", this.getPower(configuration, living, container).toTag());
	}

	@Override
	public void deserialize(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, PowerContainer container, CompoundTag tag) {
		if (container.getOwner() instanceof LivingEntity living)
			this.getPower(configuration, living, container).fromTag(tag);
	}
}
