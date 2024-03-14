package io.github.edwinmindcraft.apoli.common.registry.action;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.action.entity.AreaOfEffectAction;
import io.github.apace100.apoli.action.entity.CraftingTableAction;
import io.github.apace100.apoli.action.entity.EnderChestAction;
import io.github.apace100.apoli.power.factory.action.entity.RaycastAction;
import io.github.apace100.apoli.power.factory.action.entity.SpawnParticlesAction;
import io.github.apace100.apoli.power.factory.action.entity.SwingHandAction;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.action.configuration.ChangeResourceConfiguration;
import io.github.edwinmindcraft.apoli.common.action.entity.*;
import io.github.edwinmindcraft.apoli.common.action.meta.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.RegistryObject;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.ENTITY_ACTIONS;

public class ApoliEntityActions {
	public static final BiConsumer<ConfiguredEntityAction<?, ?>, Entity> EXECUTOR = ConfiguredEntityAction::execute;
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, Entity> PREDICATE = ConfiguredEntityCondition::check;
	public static final Predicate<Entity> SERVERSIDE_PREDICATE = (entity) -> !entity.level().isClientSide;

	private static <U extends EntityAction<?>> DeferredHolder<EntityAction<?>, U> of(String name) {
		return RegistryObject.create(Apoli.identifier(name), ApoliRegistries.ENTITY_ACTION_KEY.location(), Apoli.MODID);
	}

	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<ExecuteMultipleConfiguration<ConfiguredEntityAction<?, ?>, Entity>>> AND = of("and");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<ChanceConfiguration<ConfiguredEntityAction<?, ?>, Entity>>> CHANCE = of("chance");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<IfElseConfiguration<ConfiguredEntityCondition<?, ?>, ConfiguredEntityAction<?, ?>, Entity>>> IF_ELSE = of("if_else");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<IfElseListConfiguration<ConfiguredEntityCondition<?, ?>, ConfiguredEntityAction<?, ?>, Entity>>> IF_ELSE_LIST = of("if_else_list");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<ChoiceConfiguration<ConfiguredEntityAction<?, ?>, Entity>>> CHOICE = of("choice");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<DelayAction<ConfiguredEntityAction<?, ?>, Entity>>> DELAY = of("delay");
	public static final DeferredHolder<EntityAction<?>, DelegatedEntityAction<NothingConfiguration<Entity>>> NOTHING = of("nothing");

	public static final DeferredHolder<EntityAction<?>, AddVelocityAction> ADD_VELOCITY = ENTITY_ACTIONS.register("add_velocity", AddVelocityAction::new);
	public static final DeferredHolder<EntityAction<?>, AddExperienceAction> ADD_EXPERIENCE = ENTITY_ACTIONS.register("add_xp", AddExperienceAction::new);
	public static final DeferredHolder<EntityAction<?>, ApplyEffectAction> APPLY_EFFECT = ENTITY_ACTIONS.register("apply_effect", ApplyEffectAction::new);
	public static final DeferredHolder<EntityAction<?>, BlockActionAtAction> BLOCK_ACTION_AT = ENTITY_ACTIONS.register("block_action_at", BlockActionAtAction::new);
	public static final DeferredHolder<EntityAction<?>, ChangeResourceAction> CHANGE_RESOURCE = ENTITY_ACTIONS.register("change_resource", () -> new ChangeResourceAction(ChangeResourceConfiguration.ANY_CODEC));
	public static final DeferredHolder<EntityAction<?>, ClearEffectAction> CLEAR_EFFECT = ENTITY_ACTIONS.register("clear_effect", ClearEffectAction::new);
	public static final DeferredHolder<EntityAction<?>, SimpleEntityAction> EXTINGUISH = ENTITY_ACTIONS.register("extinguish", () -> new SimpleEntityAction(Entity::clearFire));
	public static final DeferredHolder<EntityAction<?>, ExecuteCommandEntityAction> EXECUTE_COMMAND = ENTITY_ACTIONS.register("execute_command", ExecuteCommandEntityAction::new);
	public static final DeferredHolder<EntityAction<?>, IntegerEntityAction> SET_ON_FIRE = ENTITY_ACTIONS.register("set_on_fire", () -> new IntegerEntityAction(Entity::setSecondsOnFire, "duration"));
	public static final DeferredHolder<EntityAction<?>, FloatEntityAction> EXHAUST = ENTITY_ACTIONS.register("exhaust", () -> FloatEntityAction.ofPlayer((x, f) -> x.getFoodData().addExhaustion(f), "amount"));
	public static final DeferredHolder<EntityAction<?>, FloatEntityAction> HEAL = ENTITY_ACTIONS.register("heal", () -> FloatEntityAction.ofLiving(LivingEntity::heal, "amount"));
	public static final DeferredHolder<EntityAction<?>, IntegerEntityAction> GAIN_AIR = ENTITY_ACTIONS.register("gain_air", () -> IntegerEntityAction.ofLiving((x, f) -> x.setAirSupply(Math.min(x.getAirSupply() + f, x.getMaxAirSupply())), "value"));
	public static final DeferredHolder<EntityAction<?>, FloatEntityAction> SET_FALL_DISTANCE = ENTITY_ACTIONS.register("set_fall_distance", () -> new FloatEntityAction((entity, f) -> entity.fallDistance = f, "fall_distance"));
	public static final DeferredHolder<EntityAction<?>, DamageAction> DAMAGE = ENTITY_ACTIONS.register("damage", DamageAction::new);
	public static final DeferredHolder<EntityAction<?>, EquippedItemAction> EQUIPPED_ITEM_ACTION = ENTITY_ACTIONS.register("equipped_item_action", EquippedItemAction::new);
	public static final DeferredHolder<EntityAction<?>, FeedAction> FEED = ENTITY_ACTIONS.register("feed", FeedAction::new);
	public static final DeferredHolder<EntityAction<?>, GiveAction> GIVE = ENTITY_ACTIONS.register("give", GiveAction::new);
	public static final DeferredHolder<EntityAction<?>, PlaySoundAction> PLAY_SOUND = ENTITY_ACTIONS.register("play_sound", PlaySoundAction::new);
	public static final DeferredHolder<EntityAction<?>, SpawnEffectCloudAction> SPAWN_EFFECT_CLOUD = ENTITY_ACTIONS.register("spawn_effect_cloud", SpawnEffectCloudAction::new);
	public static final DeferredHolder<EntityAction<?>, SpawnEntityAction> SPAWN_ENTITY = ENTITY_ACTIONS.register("spawn_entity", SpawnEntityAction::new);
	public static final DeferredHolder<EntityAction<?>, TriggerCooldownAction> TRIGGER_COOLDOWN = ENTITY_ACTIONS.register("trigger_cooldown", TriggerCooldownAction::new);

	public static final DeferredHolder<EntityAction<?>, ToggleAction> TOGGLE = ENTITY_ACTIONS.register("toggle", ToggleAction::new);
	public static final DeferredHolder<EntityAction<?>, EmitGameEventAction> EMIT_GAME_EVENT = ENTITY_ACTIONS.register("emit_game_event", EmitGameEventAction::new);
	public static final DeferredHolder<EntityAction<?>, ChangeResourceAction> SET_RESOURCE = ENTITY_ACTIONS.register("set_resource", () -> new ChangeResourceAction(ChangeResourceConfiguration.SET_CODEC));
	public static final DeferredHolder<EntityAction<?>, GrantPowerAction> GRANT_POWER = ENTITY_ACTIONS.register("grant_power", GrantPowerAction::new);
	public static final DeferredHolder<EntityAction<?>, RevokePowerAction> REVOKE_POWER = ENTITY_ACTIONS.register("revoke_power", RevokePowerAction::new);
	public static final DeferredHolder<EntityAction<?>, ExplodeAction> EXPLODE = ENTITY_ACTIONS.register("explode", ExplodeAction::new);
	public static final DeferredHolder<EntityAction<?>, SimpleEntityAction> DISMOUNT = ENTITY_ACTIONS.register("dismount", () -> new SimpleEntityAction(Entity::stopRiding));
	public static final DeferredHolder<EntityAction<?>, PassengerAction> PASSENGER_ACTION = ENTITY_ACTIONS.register("passenger_action", PassengerAction::new);
	public static final DeferredHolder<EntityAction<?>, RidingAction> RIDING_ACTION = ENTITY_ACTIONS.register("riding_action", RidingAction::new);
	public static final DeferredHolder<EntityAction<?>, AreaOfEffectAction> AREA_OF_EFFECT = ENTITY_ACTIONS.register("area_of_effect", AreaOfEffectAction::new);
	public static final DeferredHolder<EntityAction<?>, CraftingTableAction> CRAFTING_TABLE = ENTITY_ACTIONS.register("crafting_table", CraftingTableAction::new);
	public static final DeferredHolder<EntityAction<?>, EnderChestAction> ENDER_CHEST = ENTITY_ACTIONS.register("ender_chest", EnderChestAction::new);
	public static final DeferredHolder<EntityAction<?>, SwingHandAction> SWING_HAND = ENTITY_ACTIONS.register("swing_hand", SwingHandAction::new);
	public static final DeferredHolder<EntityAction<?>, RaycastAction> RAYCAST = ENTITY_ACTIONS.register("raycast", RaycastAction::new);
	public static final DeferredHolder<EntityAction<?>, SpawnParticlesAction> SPAWN_PARTICLES = ENTITY_ACTIONS.register("spawn_particles", SpawnParticlesAction::new);
	public static final DeferredHolder<EntityAction<?>, ModifyDeathTicksAction> MODIFY_DEATH_TICKS = ENTITY_ACTIONS.register("modify_death_ticks", ModifyDeathTicksAction::new);
	public static final DeferredHolder<EntityAction<?>, ModifyResourceAction> MODIFY_RESOURCE = ENTITY_ACTIONS.register("modify_resource", ModifyResourceAction::new);
	public static final DeferredHolder<EntityAction<?>, DropInventoryAction> DROP_INVENTORY = ENTITY_ACTIONS.register("drop_inventory", DropInventoryAction::new);
	public static final DeferredHolder<EntityAction<?>, ModifyInventoryAction> MODIFY_INVENTORY = ENTITY_ACTIONS.register("modify_inventory", ModifyInventoryAction::new);
	public static final DeferredHolder<EntityAction<?>, ReplaceInventoryAction> REPLACE_INVENTORY = ENTITY_ACTIONS.register("replace_inventory", ReplaceInventoryAction::new);
	public static final DeferredHolder<EntityAction<?>, ModifyStatAction> MODIFY_STAT = ENTITY_ACTIONS.register("modify_stat", ModifyStatAction::new);
	public static final DeferredHolder<EntityAction<?>, AdvancementAction> GRANT_ADVANCEMENT = ENTITY_ACTIONS.register("grant_advancement", AdvancementAction::grant);
	public static final DeferredHolder<EntityAction<?>, AdvancementAction> REVOKE_ADVANCEMENT = ENTITY_ACTIONS.register("revoke_advancement", AdvancementAction::revoke);
	public static final DeferredHolder<EntityAction<?>, SelectorAction> SELECTOR = ENTITY_ACTIONS.register("selector", SelectorAction::new);
	public static final DeferredHolder<EntityAction<?>, FireProjectileAction> FIRE_PROJECTILE = ENTITY_ACTIONS.register("fire_projectile", FireProjectileAction::new);

	@SafeVarargs
	public static ConfiguredEntityAction<?, ?> and(HolderSet<ConfiguredEntityAction<?, ?>>... conditions) {
		return AND.get().configure(new ExecuteMultipleConfiguration<>(Arrays.asList(conditions), EXECUTOR));
	}

	public static ConfiguredEntityAction<?, ?> and(ConfiguredEntityAction<?, ?>... conditions) {
		return and(HolderSet.direct(Holder::direct, conditions));
	}


	public static void bootstrap() {
		MetaFactories.defineMetaActions(ENTITY_ACTIONS, DelegatedEntityAction::new, ConfiguredEntityAction.CODEC_SET, ConfiguredEntityCondition.CODEC_SET, ConfiguredEntityAction::optional, EXECUTOR, PREDICATE, SERVERSIDE_PREDICATE);
	}
}
