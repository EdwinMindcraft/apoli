package io.github.edwinmindcraft.apoli.common.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.MovingEntity;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.mixin.EntityAccessor;
import io.github.apace100.apoli.power.factory.condition.DistanceFromCoordinatesConditionRegistry;
import io.github.apace100.apoli.power.factory.condition.entity.ElytraFlightPossibleCondition;
import io.github.apace100.apoli.power.factory.condition.entity.RaycastCondition;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.condition.entity.*;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConditionStreamConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters;
import io.github.edwinmindcraft.calio.api.ability.AbilityHolder;
import io.github.edwinmindcraft.calio.api.ability.PlayerAbility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ApoliEntityConditions {
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, Entity> PREDICATE = ConfiguredEntityCondition::check;

	private static <U extends EntityCondition<?>> DeferredHolder<EntityCondition<?>, U> of(String name) {
		return DeferredHolder.create(ApoliRegistries.ENTITY_CONDITION_KEY.location(), Apoli.identifier(name));
	}

	public static final DeferredHolder<EntityCondition<?>, DelegatedEntityCondition<ConstantConfiguration<Entity>>> CONSTANT = of("constant");
	public static final DeferredHolder<EntityCondition<?>, DelegatedEntityCondition<ConditionStreamConfiguration<ConfiguredEntityCondition<?, ?>, Entity>>> AND = of("and");
	public static final DeferredHolder<EntityCondition<?>, DelegatedEntityCondition<ConditionStreamConfiguration<ConfiguredEntityCondition<?, ?>, Entity>>> OR = of("or");

	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> DAYTIME = register("daytime", entity -> entity.level().getDayTime() % 24000L < 13000L);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> FALL_FLYING = registerLiving("fall_flying", LivingEntity::isFallFlying);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> EXPOSED_TO_SUN = register("exposed_to_sun", SimpleEntityCondition::isExposedToSun);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> IN_RAIN = register("in_rain", x -> ((EntityAccessor) x).callIsBeingRainedOn());
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> INVISIBLE = register("invisible", Entity::isInvisible);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> ON_FIRE = register("on_fire", Entity::isOnFire);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> EXPOSED_TO_SKY = register("exposed_to_sky", SimpleEntityCondition::isExposedToSky);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> SNEAKING = register("sneaking", Entity::isShiftKeyDown);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> SPRINTING = register("sprinting", Entity::isSprinting);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> SWIMMING = register("swimming", Entity::isSwimming);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> COLLIDED_HORIZONTALLY = register("collided_horizontally", t -> t.horizontalCollision);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> CLIMBING = registerLiving("climbing", living -> living.onClimbable() || PowerContainer.hasPower(living, ApoliPowers.CLIMBING.get()));
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> TAMED = register("tamed", x -> x instanceof TamableAnimal te && te.isTame());
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> MOVING = register("moving", x -> ((MovingEntity) x).isMoving());
	public static final DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> BRIGHTNESS = registerFloat("brightness", Entity::getLightLevelDependentMagicValue);
	public static final DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> SATURATION_LEVEL = registerFloat("saturation_level", x -> x instanceof Player ? ((Player) x).getFoodData().getSaturationLevel() : null);
	public static final DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> HEALTH = registerFloatLiving("health", LivingEntity::getHealth);
	public static final DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> RELATIVE_HEALTH = registerFloatLiving("relative_health", t -> t.getHealth() / t.getMaxHealth());
	public static final DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> FALL_DISTANCE = registerFloat("fall_distance", t -> t.fallDistance);
	public static final DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> TIME_OF_DAY = registerInt("time_of_day", t -> Math.toIntExact(t.level().getDayTime() % 24000L));
	public static final DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> AIR = registerInt("air", Entity::getAirSupply);
	public static final DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> FOOD_LEVEL = registerIntPlayer("food_level", x -> x.getFoodData().getFoodLevel());
	public static final DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> XP_LEVELS = registerIntPlayer("xp_levels", x -> x.experienceLevel);
	public static final DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> XP_POINTS = registerIntPlayer("xp_points", x -> x.totalExperience);
	public static final DeferredHolder<EntityCondition<?>, EnchantmentCondition> ENCHANTMENT = register("enchantment", EnchantmentCondition::new);
	public static final DeferredHolder<EntityCondition<?>, BlockCollisionCondition> BLOCK_COLLISION = register("block_collision", BlockCollisionCondition::new);
	public static final DeferredHolder<EntityCondition<?>, PowerActiveCondition> POWER_ACTIVE = register("power_active", PowerActiveCondition::new);
	public static final DeferredHolder<EntityCondition<?>, StatusEffectCondition> STATUS_EFFECT = register("status_effect", StatusEffectCondition::new);
	public static final DeferredHolder<EntityCondition<?>, SubmergedInCondition> SUBMERGED_IN = register("submerged_in", SubmergedInCondition::new);
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<EntityType<?>>> ENTITY_TYPE = register("entity_type", SerializableDataTypes.ENTITY_TYPE.fieldOf("entity_type"), (entity, o) -> Objects.equals(o, entity.getType()));
	public static final DeferredHolder<EntityCondition<?>, InTagCondition> IN_TAG = register("in_tag", InTagCondition::new);
	public static final DeferredHolder<EntityCondition<?>, PowerCondition> POWER = register("power", PowerCondition::new);
	public static final DeferredHolder<EntityCondition<?>, FluidHeightCondition> FLUID_HEIGHT = register("fluid_height", FluidHeightCondition::new);
	public static final DeferredHolder<EntityCondition<?>, OnBlockCondition> ON_BLOCK = register("on_block", OnBlockCondition::new);
	public static final DeferredHolder<EntityCondition<?>, HolderBasedEntityCondition<ConfiguredBlockCondition<?, ?>>> IN_BLOCK = register("in_block", () -> HolderBasedEntityCondition.required(ConfiguredBlockCondition.required("block_condition"), (entity, configuration) -> ConfiguredBlockCondition.check(configuration, entity.level(), entity.blockPosition())));
	public static final DeferredHolder<EntityCondition<?>, ResourceCondition> RESOURCE = register("resource", ResourceCondition::new);
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<ResourceKey<Level>>> DIMENSION = register("dimension", SerializableDataTypes.DIMENSION.fieldOf("dimension"), (entity, dimension) -> entity.getCommandSenderWorld().dimension().equals(dimension));
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<MobType>> ENTITY_GROUP = register("entity_group", SerializableDataTypes.ENTITY_GROUP.fieldOf("group"), (entity, group) -> entity instanceof LivingEntity living && living.getMobType().equals(group));
	public static final DeferredHolder<EntityCondition<?>, HolderBasedEntityCondition<ConfiguredItemCondition<?, ?>>> USING_ITEM = register("using_item", () -> HolderBasedEntityCondition.optional(ConfiguredItemCondition.optional("item_condition"), (entity, configuration) -> entity instanceof LivingEntity living && living.isUsingItem() && ConfiguredItemCondition.check(configuration, living.level(), living.getItemInHand(living.getUsedItemHand()))));
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<ResourceLocation>> PREDICATE_CONDITION = register("predicate", ResourceLocation.CODEC.fieldOf("predicate"), SingleFieldEntityCondition::checkPredicate);
	public static final DeferredHolder<EntityCondition<?>, EquippedItemCondition> EQUIPPED_ITEM = register("equipped_item", EquippedItemCondition::new);
	public static final DeferredHolder<EntityCondition<?>, CommandCondition> COMMAND = register("command", CommandCondition::new);
	public static final DeferredHolder<EntityCondition<?>, AttributeCondition> ATTRIBUTE = register("attribute", AttributeCondition::new);
	public static final DeferredHolder<EntityCondition<?>, BlockInRadiusCondition> BLOCK_IN_RADIUS = register("block_in_radius", BlockInRadiusCondition::new);
	public static final DeferredHolder<EntityCondition<?>, BiomeCondition> BIOME = register("biome", BiomeCondition::new);
	public static final DeferredHolder<EntityCondition<?>, ScoreboardCondition> SCOREBOARD = register("scoreboard", ScoreboardCondition::new);
	public static final DeferredHolder<EntityCondition<?>, InBlockAnywhereCondition> IN_BLOCK_ANYWHERE = register("in_block_anywhere", InBlockAnywhereCondition::new);
	public static final DeferredHolder<EntityCondition<?>, UsingEffectiveToolCondition> USING_EFFECTIVE_TOOL = registerSided("using_effective_tool", () -> UsingEffectiveToolCondition.Client::new, () -> UsingEffectiveToolCondition::new);
	public static final DeferredHolder<EntityCondition<?>, AdvancementCondition> ADVANCEMENT = registerSided("advancement", () -> AdvancementCondition.Client::new, () -> AdvancementCondition::new);
	public static final DeferredHolder<EntityCondition<?>, GameModeCondition> GAMEMODE = registerSided("gamemode", () -> GameModeCondition.Client::new, () -> GameModeCondition::new);

	public static final DeferredHolder<EntityCondition<?>, RaycastCondition> RAYCAST = register("raycast", RaycastCondition::new);
	public static final DeferredHolder<EntityCondition<?>, ElytraFlightPossibleCondition> ELYTRA_FLIGHT_POSSIBLE = register("elytra_flight_possible", ElytraFlightPossibleCondition::new);
	public static final DeferredHolder<EntityCondition<?>, BiEntityWrappedCondition> RIDING = register("riding", () -> new BiEntityWrappedCondition(BiEntityWrappedCondition::riding));
	public static final DeferredHolder<EntityCondition<?>, BiEntityWrappedCondition> RIDING_ROOT = register("riding_root", () -> new BiEntityWrappedCondition(BiEntityWrappedCondition::ridingRoot));
	public static final DeferredHolder<EntityCondition<?>, IntComparingBECEntityCondition> RIDING_RECURSIVE = register("riding_recursive", () -> new IntComparingBECEntityCondition(IntComparingBECEntityCondition::ridingRecursive));
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> LIVING = register("living", e -> e instanceof LivingEntity);
	public static final DeferredHolder<EntityCondition<?>, IntComparingBECEntityCondition> PASSENGER = register("passenger", () -> new IntComparingBECEntityCondition(IntComparingBECEntityCondition::passenger));
	public static final DeferredHolder<EntityCondition<?>, IntComparingBECEntityCondition> PASSENGER_RECURSIVE = register("passenger_recursive", () -> new IntComparingBECEntityCondition(IntComparingBECEntityCondition::passengerRecursive));
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<CompoundTag>> NBT = register("nbt", SerializableDataTypes.NBT.fieldOf("nbt"), SingleFieldEntityCondition::nbt);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> EXISTS = register("exists", Objects::nonNull);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> CREATIVE_FLYING = registerPlayer("creative_flying", x -> x.getAbilities().flying);
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<PowerFactory<?>>> POWER_TYPE = register("power_type", PowerFactory.CODEC.fieldOf("power_type"), PowerContainer::hasPower);
	public static final DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<PlayerAbility>> ABILITY = register("ability", ApoliDataTypes.PLAYER_ABILITY.fieldOf("ability"), AbilityHolder::has);
	public static final DeferredHolder<EntityCondition<?>, SimpleEntityCondition> GLOWING = register("glowing", SimpleEntityCondition::isGlowing);
	public static final DeferredHolder<EntityCondition<?>, InventoryCondition> INVENTORY = register("inventory", InventoryCondition::new);

	public static ConfiguredEntityCondition<?, ?> constant(boolean value) {
		return CONSTANT.get().configure(new ConstantConfiguration<>(value));
	}

	@SafeVarargs
	public static ConfiguredEntityCondition<?, ?> and(HolderSet<ConfiguredEntityCondition<?, ?>>... conditions) {
		return AND.get().configure(ConditionStreamConfiguration.and(Arrays.asList(conditions), PREDICATE));
	}

	public static ConfiguredEntityCondition<?, ?> and(ConfiguredEntityCondition<?, ?>... conditions) {
		return and(HolderSet.direct(Holder::direct, conditions));
	}

	@SafeVarargs
	public static ConfiguredEntityCondition<?, ?> or(HolderSet<ConfiguredEntityCondition<?, ?>>... conditions) {
		return OR.get().configure(ConditionStreamConfiguration.or(Arrays.asList(conditions), PREDICATE));
	}

	public static ConfiguredEntityCondition<?, ?> or(ConfiguredEntityCondition<?, ?>... conditions) {
		return or(HolderSet.direct(Holder::direct, conditions));
	}

	public static void bootstrap() {
		MetaFactories.defineMetaConditions(ApoliRegisters.ENTITY_CONDITIONS, DelegatedEntityCondition::new, ConfiguredEntityCondition.CODEC_SET, PREDICATE);
		DistanceFromCoordinatesConditionRegistry.registerEntityCondition();
	}

	private static <T extends EntityCondition<?>> DeferredHolder<EntityCondition<?>, T> register(String name, Supplier<T> factory) {
		return ApoliRegisters.ENTITY_CONDITIONS.register(name, factory);
	}

	private static DeferredHolder<EntityCondition<?>, SimpleEntityCondition> register(String name, Predicate<Entity> factory) {
		return register(name, () -> new SimpleEntityCondition(factory));
	}

	private static DeferredHolder<EntityCondition<?>, SimpleEntityCondition> registerLiving(String name, Predicate<LivingEntity> factory) {
		return register(name, () -> new SimpleEntityCondition(x -> x instanceof LivingEntity living && factory.test(living)));
	}

	private static DeferredHolder<EntityCondition<?>, SimpleEntityCondition> registerPlayer(String name, Predicate<Player> factory) {
		return register(name, () -> new SimpleEntityCondition(x -> x instanceof Player player && factory.test(player)));
	}

	private static DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> registerInt(String name, Function<Entity, Integer> factory) {
		return register(name, () -> new IntComparingEntityCondition(factory));
	}

	private static DeferredHolder<EntityCondition<?>, IntComparingEntityCondition> registerIntPlayer(String name, Function<Player, Integer> factory) {
		return registerInt(name, living -> living instanceof Player pe ? factory.apply(pe) : null);
	}

	private static DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> registerFloat(String name, Function<Entity, Float> factory) {
		return register(name, () -> new FloatComparingEntityCondition(factory));
	}

	private static DeferredHolder<EntityCondition<?>, FloatComparingEntityCondition> registerFloatLiving(String name, Function<LivingEntity, Float> factory) {
		return register(name, () -> new FloatComparingEntityCondition(x -> x instanceof LivingEntity living ? factory.apply(living) : null));
	}

	private static <T> DeferredHolder<EntityCondition<?>, SingleFieldEntityCondition<T>> register(String name, MapCodec<T> codec, BiPredicate<Entity, T> predicate) {
		return register(name, () -> new SingleFieldEntityCondition<>(codec, predicate));
	}

	private static <T extends EntityCondition<?>> DeferredHolder<EntityCondition<?>, T> registerSided(String name, Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
		return register(name, () -> {
			if (FMLEnvironment.dist == Dist.CLIENT)
				return client.get().get();
			else
				return server.get().get();
		});
	}
}
