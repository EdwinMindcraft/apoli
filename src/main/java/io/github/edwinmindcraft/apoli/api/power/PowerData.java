package io.github.edwinmindcraft.apoli.api.power;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record PowerData(List<ConfiguredEntityCondition<?, ?>> conditions, boolean hidden, int loadingPriority,
						String name, String description) {

	public static final PowerData DEFAULT = new PowerData(ImmutableList.of(), false, 0, "", "");

	public static final MapCodec<PowerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.listOf(ConfiguredEntityCondition.CODEC, "condition", "conditions").forGetter(PowerData::conditions),
			CalioCodecHelper.optionalField(Codec.BOOL, "hidden", false).forGetter(PowerData::hidden),
			CalioCodecHelper.optionalField(Codec.INT, "loading_priority", 0).forGetter(PowerData::loadingPriority),
			CalioCodecHelper.optionalField(Codec.STRING, "name", "").forGetter(PowerData::name),
			CalioCodecHelper.optionalField(Codec.STRING, "description", "").forGetter(PowerData::description)
	).apply(instance, PowerData::new));

	public static Builder builder() {
		return new Builder();
	}

	public TranslatableComponent getName() {
		return new TranslatableComponent(this.name());
	}

	public TranslatableComponent getDescription() {
		return new TranslatableComponent(this.description());
	}

	/**
	 * Completes the current definition of the power by adding the name if it couldn't be found.<br/>
	 * This is solely for use during power loading, everything else should be fine.
	 *
	 * @param identifier The identifier of this power.
	 *
	 * @return A new, completed power data.
	 */
	public PowerData complete(ResourceLocation identifier) {
		return new PowerData(this.conditions, this.hidden, this.loadingPriority,
				StringUtils.isEmpty(this.name) ? "power." + identifier.getNamespace() + "." + identifier.getPath() + ".name" : this.name,
				StringUtils.isEmpty(this.description) ? "power." + identifier.getNamespace() + "." + identifier.getPath() + ".description" : this.description);
	}

	public Builder copyOf() {
		Builder builder = builder().hidden(this.hidden).withPriority(this.loadingPriority)
				.withName(this.name).withDescription(this.description);
		this.conditions.forEach(builder::addCondition);
		return builder;
	}

	public static class Builder {
		private final List<ConfiguredEntityCondition<?, ?>> conditions = new ArrayList<>();
		private boolean hidden = false;
		private int loadingPriority = 0;
		private String name = "";
		private String description = "";

		public Builder() {

		}

		public List<ConfiguredEntityCondition<?, ?>> getConditions() {
			return this.conditions;
		}

		public Builder hidden() {
			this.hidden = true;
			return this;
		}

		public Builder hidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		public Builder withPriority(int priority) {
			this.loadingPriority = priority;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withIdentifier(ResourceLocation identifier) {
			this.name = "power." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			this.description = "power." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
			return this;
		}

		public Builder addCondition(ConfiguredEntityCondition<?, ?> condition) {
			this.conditions.add(condition);
			return this;
		}

		public PowerData build() {
			return new PowerData(this.conditions, this.hidden, this.loadingPriority, this.name, this.description);
		}
	}
}
