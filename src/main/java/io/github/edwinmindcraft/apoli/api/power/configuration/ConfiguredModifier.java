package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.power.ModifierData;
import io.github.edwinmindcraft.apoli.api.power.factory.ModifierOperation;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import io.github.edwinmindcraft.calio.api.network.PropagatingDefaultedOptionalFieldCodec;
import io.github.edwinmindcraft.calio.api.network.PropagatingOptionalFieldCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class ConfiguredModifier<F extends ModifierOperation> {
	public static final Codec<ConfiguredModifier<?>> CODEC = ModifierOperation.CODEC.dispatch("operation", ConfiguredModifier::getFactory, ModifierOperation::getCodec);
	public static final CodecSet<ConfiguredModifier<?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_MODIFIER_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredModifier<?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredModifier<?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Optional<Holder<ConfiguredModifier<?>>>> optional(String name) {
		return HOLDER.optionalFieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredModifier<?>>> optional(String name, Holder<ConfiguredModifier<?>> key) {
		return HOLDER.optionalFieldOf(name, key);
	}

	public static double apply(Holder<ConfiguredModifier<?>> modifier, Entity entity, double value) {
		if (!modifier.isBound())
			return value;
		else
			return modifier.value().apply(entity, value, value);
	}

	private final Lazy<F> factory;
	private final ModifierData data;

	public ConfiguredModifier(Supplier<F> factory, ModifierData data) {
		this.factory = Lazy.of(factory);
		this.data = data;
	}

	public F getFactory() {
		return factory.get();
	}

	public ModifierData getData() {
		return data;
	}

	public double apply(Entity entity, double base, double current) {
		return this.getFactory().apply(List.of(this), entity, base, current);
	}

	@Override
	public String toString() {
		return "CM:" + ApoliRegistries.MODIFIER_OPERATION.getKey(this.getFactory()) + "-" + this.getData();
	}
}
