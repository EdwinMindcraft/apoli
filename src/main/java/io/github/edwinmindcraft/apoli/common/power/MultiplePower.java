package io.github.edwinmindcraft.apoli.common.power;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.MultipleConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.function.Predicate;

public class MultiplePower extends PowerFactory<MultipleConfiguration<ConfiguredPower<?, ?>>> {
	private static final ImmutableSet<String> EXCLUDED = ImmutableSet.of("type", "loading_priority", "name", "description", "hidden", "condition", "conditions");
	private static final Predicate<String> ALLOWED = str -> !EXCLUDED.contains(str) && !ApoliAPI.isAdditionalDataField(str);

	public MultiplePower() {
		super(MultipleConfiguration.mapCodec(ConfiguredPower.CODEC, ConfiguredPower.HOLDER, ALLOWED, s -> "_" + s, MultiplePower::reconfigure).codec(), false);
	}

	@Override
	public Map<String, Holder<ConfiguredPower<?, ?>>> getContainedPowers(ConfiguredPower<MultipleConfiguration<ConfiguredPower<?, ?>>, ?> configuration) {
		return configuration.getConfiguration().children();
	}

	/**
	 * Has the effect of hiding the power from the origin screen.<br/>
	 * Additionally, this will post the event to {@link PowerLoadEvent.Post} which will be used to handle additional data.
	 */
	private static <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> ConfiguredPower<C, ?> reconfigure(String suffix, ConfiguredPower<C, F> source, JsonElement root) {
		MinecraftForge.EVENT_BUS.post(new PowerLoadEvent.Post(new ResourceLocation(SerializableData.CURRENT_NAMESPACE, SerializableData.CURRENT_PATH + suffix), root, source));
		return source.getFactory().configure(source.getConfiguration(), source.getData().copyOf().hidden().build());
	}
}
