package io.github.apace100.apoli.power.factory;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerConfiguration;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.Lazy;

import java.util.function.BiFunction;
import java.util.function.Function;

public class PowerFactory<P extends Power> {

	private final ResourceLocation id;
	private final Lazy<io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>> wrapped;
	private boolean hasConditions = false;

	public PowerFactory(ResourceLocation id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
		this.id = id;
		this.wrapped = Lazy.of(() -> new FabricPowerFactory<>(FabricPowerConfiguration.codec(data, factoryConstructor), this.hasConditions));
	}

	public PowerFactory(ResourceLocation id, io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?> factory) {
		this.id = id;
		this.wrapped = Lazy.of(() -> factory);
	}

	public io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?> getWrapped() {
		return this.wrapped.get();
	}

	public PowerFactory<P> allowCondition() {
		if (!this.hasConditions) this.hasConditions = true;
		return this;
	}

	public ResourceLocation getSerializerId() {
		return this.id;
	}

	public class Instance implements BiFunction<PowerType<P>, LivingEntity, P> {

		public final ConfiguredPower<?, ?> power;

		private Instance(ConfiguredPower<?, ?> power) {
			this.power = power;
		}

		public ConfiguredPower<?, ?> getPower() {
			return this.power;
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeResourceLocation(PowerFactory.this.id);
			buf.writeWithCodec(NbtOps.INSTANCE, ConfiguredPower.CODEC, this.power);
		}

		@SuppressWarnings("unchecked")
		@Override
		public P apply(PowerType<P> pPowerType, LivingEntity livingEntity) {
			/*BiFunction<PowerType<P>, LivingEntity, P> powerFactory = factoryConstructor.apply(dataInstance);
            P p = powerFactory.apply(pPowerType, livingEntity);
            if(hasConditions && dataInstance.isPresent("condition")) {
                p.addCondition((ConditionFactory<Entity>.Instance) dataInstance.get("condition"));
            }*/
			if (this.power.getConfiguration() instanceof FabricPowerConfiguration config)
				return (P) this.power.getPowerData(livingEntity, () -> config.power().apply(pPowerType, livingEntity));
			return null;
		}

        /*public SerializableData.Instance getDataInstance() {
            return dataInstance;
        }*/

		public PowerFactory<?> getFactory() {
			return PowerFactory.this;
		}
	}

	public Instance read(JsonObject json) {
		return new Instance(ConfiguredPower.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst).getOrThrow(false, s -> {}));
	}

	public Instance read(FriendlyByteBuf buffer) {
		return new Instance(buffer.readWithCodec(NbtOps.INSTANCE, ConfiguredPower.CODEC));
	}
}
