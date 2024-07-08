package io.github.edwinmindcraft.apoli.common.condition.entity;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.function.BiPredicate;

public class SingleFieldEntityCondition<T> extends EntityCondition<FieldConfiguration<T>> {
	public static boolean checkPredicate(Entity entity, ResourceKey<LootItemCondition> key) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			var lootCondition = server.reloadableRegistries().lookup().get(Registries.PREDICATE, key).orElse(null);
			if (lootCondition != null) {
				LootParams lootBuilder = (new LootParams.Builder((ServerLevel) entity.level()))
						.withParameter(LootContextParams.ORIGIN, entity.position())
						.withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
						.create(LootContextParamSets.COMMAND);
				return lootCondition.value().test(new LootContext.Builder(lootBuilder).create(Optional.empty()));
			}
        }
		return false;
	}

	public static boolean nbt(Entity entity, CompoundTag compoundTag) {
		CompoundTag tag = new CompoundTag();
		entity.save(tag);
		return NbtUtils.compareNbt(compoundTag, tag, true);
	}

	private final BiPredicate<Entity, T> predicate;

	public SingleFieldEntityCondition(MapCodec<T> codec, BiPredicate<Entity, T> predicate) {
		super(FieldConfiguration.codec(codec));
		this.predicate = predicate;
	}

	@Override
	public boolean check(FieldConfiguration<T> configuration, Entity entity) {
		return this.predicate.test(entity, configuration.value());
	}
}
