package dev.experimental.apoli.common.registry.action;

import dev.experimental.apoli.api.MetaFactories;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityAction;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityCondition;
import dev.experimental.apoli.common.action.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import static dev.experimental.apoli.common.registry.ApoliRegisters.ENTITY_ACTIONS;

public class ModEntityActions {
	public static final BiConsumer<ConfiguredEntityAction<?, ?>, Entity> EXECUTOR = (action, entity) -> action.execute(entity);
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, Entity> PREDICATE = (condition, entity) -> entity instanceof LivingEntity le && condition.check(le);

	public static final RegistryObject<AddVelocityAction> ADD_VELOCITY = ENTITY_ACTIONS.register("add_velocity", AddVelocityAction::new);
	public static final RegistryObject<AddExperienceAction> ADD_EXPERIENCE = ENTITY_ACTIONS.register("add_xp", AddExperienceAction::new);
	public static final RegistryObject<ApplyEffectAction> APPLY_EFFECT = ENTITY_ACTIONS.register("apply_effect", ApplyEffectAction::new);
	public static final RegistryObject<BlockActionAtAction> BLOCK_ACTION_AT = ENTITY_ACTIONS.register("block_action_at", BlockActionAtAction::new);
	public static final RegistryObject<ChangeResourceAction> CHANGE_RESOURCE = ENTITY_ACTIONS.register("change_resource", ChangeResourceAction::new);
	public static final RegistryObject<ClearEffectAction> CLEAR_EFFECT = ENTITY_ACTIONS.register("clear_effect", ClearEffectAction::new);
	public static final RegistryObject<SimpleEntityAction> EXTINGUISH = ENTITY_ACTIONS.register("extinguish", () -> new SimpleEntityAction(Entity::clearFire));
	public static final RegistryObject<ExecuteCommandEntityAction> EXECUTE_COMMAND = ENTITY_ACTIONS.register("execute_command", ExecuteCommandEntityAction::new);
	public static final RegistryObject<IntegerEntityAction> SET_ON_FIRE = ENTITY_ACTIONS.register("set_on_fire", () -> new IntegerEntityAction(Entity::setSecondsOnFire, "duration"));
	public static final RegistryObject<FloatEntityAction> EXHAUST = ENTITY_ACTIONS.register("exhaust", () -> FloatEntityAction.ofPlayer((x, f) -> x.getFoodData().addExhaustion(f), "amount"));
	public static final RegistryObject<FloatEntityAction> HEAL = ENTITY_ACTIONS.register("heal", () -> FloatEntityAction.ofLiving(LivingEntity::heal, "amount"));
	public static final RegistryObject<IntegerEntityAction> GAIN_AIR = ENTITY_ACTIONS.register("gain_air", () -> IntegerEntityAction.ofLiving((x, f) -> x.setAirSupply(x.getAirSupply() + f), "value"));
	public static final RegistryObject<FloatEntityAction> SET_FALL_DISTANCE = ENTITY_ACTIONS.register("set_fall_distance", () -> new FloatEntityAction((entity, f) -> entity.fallDistance = f, "fall_distance"));
	public static final RegistryObject<DamageAction> DAMAGE = ENTITY_ACTIONS.register("damage", DamageAction::new);
	public static final RegistryObject<EquippedItemAction> EQUIPPED_ITEM_ACTION = ENTITY_ACTIONS.register("equipped_item_action", EquippedItemAction::new);
	public static final RegistryObject<FeedAction> FEED = ENTITY_ACTIONS.register("feed", FeedAction::new);
	public static final RegistryObject<GiveAction> GIVE = ENTITY_ACTIONS.register("give", GiveAction::new);
	public static final RegistryObject<PlaySoundAction> PLAY_SOUND = ENTITY_ACTIONS.register("play_sound", PlaySoundAction::new);
	public static final RegistryObject<SpawnEffectCloudAction> SPAWN_EFFECT_CLOUD = ENTITY_ACTIONS.register("spawn_effect_cloud", SpawnEffectCloudAction::new);
	public static final RegistryObject<SpawnEntityAction> SPAWN_ENTITY = ENTITY_ACTIONS.register("spawn_entity", SpawnEntityAction::new);
	public static final RegistryObject<TriggerCooldownAction> TRIGGER_COOLDOWN = ENTITY_ACTIONS.register("trigger_cooldown", TriggerCooldownAction::new);

	public static void register() {
		MetaFactories.defineMetaActions(ENTITY_ACTIONS, DelegatedEntityAction::new, ConfiguredEntityAction.CODEC, ConfiguredEntityCondition.CODEC, EXECUTOR, PREDICATE);
	}
}
