package io.github.edwinmindcraft.apoli.common.registry.condition;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.condition.damage.*;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConditionStreamConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.DAMAGE_CONDITIONS;

public class ApoliDamageConditions {
	public static final BiPredicate<ConfiguredDamageCondition<?, ?>, Pair<DamageSource, Float>> PREDICATE = (config, pair) -> config.check(pair.getLeft(), pair.getRight());

	private static <U extends DamageCondition<?>> DeferredHolder<DamageCondition<?>, U> of(String name) {
		return DeferredHolder.create(ApoliRegistries.DAMAGE_CONDITION_KEY, Apoli.identifier(name));
	}

	public static final DeferredHolder<DamageCondition<?>, DelegatedDamageCondition<ConstantConfiguration<Pair<DamageSource, Float>>>> CONSTANT = of("constant");
	public static final DeferredHolder<DamageCondition<?>, DelegatedDamageCondition<ConditionStreamConfiguration<ConfiguredDamageCondition<?, ?>, Pair<DamageSource, Float>>>> AND = of("and");
	public static final DeferredHolder<DamageCondition<?>, DelegatedDamageCondition<ConditionStreamConfiguration<ConfiguredDamageCondition<?, ?>, Pair<DamageSource, Float>>>> OR = of("or");

	public static final DeferredHolder<DamageCondition<?>, AmountCondition> AMOUNT = DAMAGE_CONDITIONS.register("amount", AmountCondition::new);
	public static final DeferredHolder<DamageCondition<?>, NameCondition> NAME = DAMAGE_CONDITIONS.register("name", NameCondition::new);
	public static final DeferredHolder<DamageCondition<?>, InTagCondition> IN_TAG = DAMAGE_CONDITIONS.register("in_tag", InTagCondition::new);
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> FIRE = DAMAGE_CONDITIONS.register("fire", () -> new SimpleTagCondition(DamageTypeTags.IS_FIRE));
	public static final DeferredHolder<DamageCondition<?>, ProjectileCondition> PROJECTILE = DAMAGE_CONDITIONS.register("projectile", ProjectileCondition::new);
	public static final DeferredHolder<DamageCondition<?>, AttackerCondition> ATTACKER = DAMAGE_CONDITIONS.register("attacker", AttackerCondition::new);
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> BYPASSES_ARMOR = DAMAGE_CONDITIONS.register("bypasses_armor", () -> new SimpleTagCondition(DamageTypeTags.BYPASSES_ARMOR));
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> EXPLOSIVE = DAMAGE_CONDITIONS.register("explosive", () -> new SimpleTagCondition(DamageTypeTags.IS_EXPLOSION));
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> FROM_FALLING = DAMAGE_CONDITIONS.register("from_falling", () -> new SimpleTagCondition(DamageTypeTags.IS_FALL));
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> UNBLOCKABLE = DAMAGE_CONDITIONS.register("unblockable", () -> new SimpleTagCondition(DamageTypeTags.BYPASSES_SHIELD));
	public static final DeferredHolder<DamageCondition<?>, SimpleDamageCondition> OUT_OF_WORLD = DAMAGE_CONDITIONS.register("out_of_world", () -> new SimpleTagCondition(DamageTypeTags.BYPASSES_INVULNERABILITY));

	public static ConfiguredDamageCondition<?, ?> constant(boolean value) {
		return CONSTANT.get().configure(new ConstantConfiguration<>(value));
	}

	@SafeVarargs
	public static ConfiguredDamageCondition<?, ?> and(HolderSet<ConfiguredDamageCondition<?, ?>>... conditions) {
		return AND.get().configure(ConditionStreamConfiguration.and(Arrays.asList(conditions), PREDICATE));
	}

	@SafeVarargs
	public static ConfiguredDamageCondition<?, ?> or(HolderSet<ConfiguredDamageCondition<?, ?>>... conditions) {
		return OR.get().configure(ConditionStreamConfiguration.or(Arrays.asList(conditions), PREDICATE));
	}

	public static void bootstrap() {
		MetaFactories.defineMetaConditions(DAMAGE_CONDITIONS, DelegatedDamageCondition::new, ConfiguredDamageCondition.CODEC_SET, PREDICATE);
	}
}
