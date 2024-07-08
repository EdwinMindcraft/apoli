package io.github.apace100.apoli.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliLootConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;

public class PowerLootCondition implements LootItemCondition {

	public static final MapCodec<PowerLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("power").forGetter(PowerLootCondition::getPowerId),
			ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::getPowerSourceId)
	).apply(instance, PowerLootCondition::new));


	private final ResourceLocation powerId;
	private final ResourceLocation powerSourceId;

	private PowerLootCondition(ResourceLocation powerId) {
		this.powerId = powerId;
		this.powerSourceId = null;
	}

	private PowerLootCondition(ResourceLocation powerId, ResourceLocation powerSourceId) {
		this.powerId = powerId;
		this.powerSourceId = powerSourceId;
	}

	public LootItemConditionType getType() {
		return ApoliLootConditions.POWER_LOOT_CONDITION.get();
	}

	public boolean test(LootContext lootContext) {

		PowerContainer container = PowerContainer.get(
				lootContext.getParam(LootContextParams.THIS_ENTITY)
		);

		if (container != null) {

			if (powerSourceId != null) return container.hasPower(powerId, powerSourceId);
			else return container.hasPower(powerId);

		}

		return false;

	}

	public ResourceLocation getPowerId() {
		return powerId;
	}

	public Optional<ResourceLocation> getPowerSourceId() {
		return powerSourceId;
	}

}
