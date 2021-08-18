package dev.experimental.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.IFactory;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityAction;
import dev.experimental.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class EntityAction<T extends IDynamicFeatureConfiguration> extends ForgeRegistryEntry<EntityAction<?>> implements IFactory<T, ConfiguredEntityAction<T, ?>, EntityAction<T>> {
	public static final Codec<EntityAction<?>> CODEC = ApoliRegistries.codec(ApoliRegistries.ENTITY_ACTION);

	private final Codec<T> codec;

	protected EntityAction(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public final ConfiguredEntityAction<T, ?> configure(T input) {
		return new ConfiguredEntityAction<>(this, input);
	}

	public abstract void execute(T configuration, Entity entity);
}
