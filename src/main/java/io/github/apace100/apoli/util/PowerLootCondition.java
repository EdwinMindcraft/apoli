package io.github.apace100.apoli.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliLootConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;

public class PowerLootCondition implements LootItemCondition {

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

		Optional<PowerContainer> optionalPowerHolderComponent = PowerContainer.get(
				lootContext.getParam(LootContextParams.THIS_ENTITY)
		).resolve();

		if (optionalPowerHolderComponent.isPresent()) {

			PowerContainer powerHolderComponent = optionalPowerHolderComponent.get();

			if (powerSourceId != null) return powerHolderComponent.hasPower(powerId, powerSourceId);
			else return powerHolderComponent.hasPower(powerId);

		}

		return false;

	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<PowerLootCondition> {

		public void serialize(JsonObject jsonObject, PowerLootCondition powerLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("power", powerLootCondition.powerId.toString());
			if (powerLootCondition.powerSourceId != null) jsonObject.addProperty("source", powerLootCondition.powerSourceId.toString());
		}

		public PowerLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			ResourceLocation power = new ResourceLocation(GsonHelper.getAsString(jsonObject, "power"));
			if (jsonObject.has("source")) {
				ResourceLocation source = new ResourceLocation(GsonHelper.getAsString(jsonObject, "source"));
				return new PowerLootCondition(power, source);
			}
			return new PowerLootCondition(power);
		}

	}

}
