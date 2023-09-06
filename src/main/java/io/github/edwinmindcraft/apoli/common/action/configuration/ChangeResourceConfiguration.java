package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record ChangeResourceConfiguration(Holder<ConfiguredPower<?, ?>> resource,
										  int amount,
										  ResourceOperation operation) implements IDynamicFeatureConfiguration {

	public static final Codec<ChangeResourceConfiguration> ANY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredPower.CODEC_SET.holderRef().fieldOf("resource").forGetter(ChangeResourceConfiguration::resource),
			CalioCodecHelper.INT.fieldOf("change").forGetter(ChangeResourceConfiguration::amount),
			CalioCodecHelper.optionalField(ApoliDataTypes.RESOURCE_OPERATION, "operation", ResourceOperation.ADD).forGetter(ChangeResourceConfiguration::operation)
	).apply(instance, ChangeResourceConfiguration::new));


	public static final Codec<ChangeResourceConfiguration> SET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredPower.CODEC_SET.holderRef().fieldOf("resource").forGetter(ChangeResourceConfiguration::resource),
			CalioCodecHelper.INT.fieldOf("value").forGetter(ChangeResourceConfiguration::amount)
	).apply(instance, (resource, amount) -> new ChangeResourceConfiguration(resource, amount, ResourceOperation.SET)));

	@Override
	public boolean isConfigurationValid() {
		return this.amount() != 0;
	}

	@Override
	public String toString() {
		return "ChangeResourceConfiguration{" +
                "resource=" + (this.resource() instanceof Holder.Reference<ConfiguredPower<?, ?>> ref ? ref.isBound() ? ref.key() : "Unbound" : "Unsupported") +
                ", amount=" + this.amount +
                ", operation=" + this.operation +
                '}';
	}
}
