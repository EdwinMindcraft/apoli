package io.github.apace100.apoli.util;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GainedPowerCriterion extends SimpleCriterionTrigger<GainedPowerCriterion.Conditions> {

	public static GainedPowerCriterion INSTANCE = new GainedPowerCriterion();

	private static final ResourceLocation ID = Apoli.identifier("gained_power");

	@Override
	protected @NotNull Conditions createInstance(@NotNull JsonObject obj, @NotNull ContextAwarePredicate composite, @NotNull DeserializationContext context) {
		ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(obj, "power"));
		return new Conditions(composite, id);
	}

	public void trigger(ServerPlayer player, ResourceKey<ConfiguredPower<?, ?>> type) {
		this.trigger(player, (conditions -> conditions.matches(type)));
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ResourceLocation powerId;

		public Conditions(ContextAwarePredicate player, ResourceLocation powerId) {
			super(GainedPowerCriterion.ID, player);
			this.powerId = powerId;
		}

		public boolean matches(ResourceKey<ConfiguredPower<?, ?>> powerType) {
			return Objects.equals(powerType.location(), this.powerId);
		}

		@Override
		public @NotNull JsonObject serializeToJson(@NotNull SerializationContext context) {
			JsonObject object = super.serializeToJson(context);
			object.addProperty("power", this.powerId.toString());
			return object;
		}
	}
}
