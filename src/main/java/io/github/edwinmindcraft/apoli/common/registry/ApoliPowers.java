package io.github.edwinmindcraft.apoli.common.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import io.github.edwinmindcraft.apoli.api.power.PowerData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.*;
import io.github.edwinmindcraft.apoli.common.power.configuration.BiEntityInteractionConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.MultipleConfiguration;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ApoliPowers {
	public static final DeferredHolder<PowerFactory<?>, ActionOnBlockBreakPower> ACTION_ON_BLOCK_BREAK = ApoliRegisters.POWER_FACTORIES.register("action_on_block_break", ActionOnBlockBreakPower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnCallbackPower> ACTION_ON_CALLBACK = ApoliRegisters.POWER_FACTORIES.register("action_on_callback", ActionOnCallbackPower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnItemUsePower> ACTION_ON_ITEM_USE = ApoliRegisters.POWER_FACTORIES.register("action_on_item_use", ActionOnItemUsePower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnLandPower> ACTION_ON_LAND = ApoliRegisters.POWER_FACTORIES.register("action_on_land", ActionOnLandPower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnWakeUpPower> ACTION_ON_WAKE_UP = ApoliRegisters.POWER_FACTORIES.register("action_on_wake_up", ActionOnWakeUpPower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOverTimePower> ACTION_OVER_TIME = ApoliRegisters.POWER_FACTORIES.register("action_over_time", ActionOverTimePower::new);
	public static final DeferredHolder<PowerFactory<?>, ActiveSelfPower> ACTIVE_SELF = ApoliRegisters.POWER_FACTORIES.register("active_self", ActiveSelfPower::new);
	public static final DeferredHolder<PowerFactory<?>, AttackerActionWhenHitPower> ATTACKER_ACTION_WHEN_HIT = ApoliRegisters.POWER_FACTORIES.register("attacker_action_when_hit", AttackerActionWhenHitPower::new);
	public static final DeferredHolder<PowerFactory<?>, AttributePower> ATTRIBUTE = ApoliRegisters.POWER_FACTORIES.register("attribute", AttributePower::new);
	public static final DeferredHolder<PowerFactory<?>, BurnPower> BURN = ApoliRegisters.POWER_FACTORIES.register("burn", BurnPower::new);
	public static final DeferredHolder<PowerFactory<?>, ClimbingPower> CLIMBING = ApoliRegisters.POWER_FACTORIES.register("climbing", ClimbingPower::new);
	public static final DeferredHolder<PowerFactory<?>, ConditionedAttributePower> CONDITIONED_ATTRIBUTE = ApoliRegisters.POWER_FACTORIES.register("conditioned_attribute", ConditionedAttributePower::new);
	public static final DeferredHolder<PowerFactory<?>, ConditionedRestrictArmorPower> CONDITIONED_RESTRICT_ARMOR = ApoliRegisters.POWER_FACTORIES.register("conditioned_restrict_armor", ConditionedRestrictArmorPower::new);
	public static final DeferredHolder<PowerFactory<?>, CooldownPower> COOLDOWN = ApoliRegisters.POWER_FACTORIES.register("cooldown", CooldownPower::new);
	public static final DeferredHolder<PowerFactory<?>, CreativeFlightPower> CREATIVE_FLIGHT = ApoliRegisters.POWER_FACTORIES.register("creative_flight", CreativeFlightPower::new);
	public static final DeferredHolder<PowerFactory<?>, DamageOverTimePower> DAMAGE_OVER_TIME = ApoliRegisters.POWER_FACTORIES.register("damage_over_time", DamageOverTimePower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> DISABLE_REGEN = ApoliRegisters.POWER_FACTORIES.register("disable_regen", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, EffectImmunityPower> EFFECT_IMMUNITY = ApoliRegisters.POWER_FACTORIES.register("effect_immunity", EffectImmunityPower::new);
	public static final DeferredHolder<PowerFactory<?>, ElytraFlightPower> ELYTRA_FLIGHT = ApoliRegisters.POWER_FACTORIES.register("elytra_flight", ElytraFlightPower::new);
	public static final DeferredHolder<PowerFactory<?>, EntityGlowPower> ENTITY_GLOW = ApoliRegisters.POWER_FACTORIES.register("entity_glow", () -> new EntityGlowPower(false));
	public static final DeferredHolder<PowerFactory<?>, EntityGroupPower> ENTITY_GROUP = ApoliRegisters.POWER_FACTORIES.register("entity_group", EntityGroupPower::new);
	public static final DeferredHolder<PowerFactory<?>, ExhaustOverTimePower> EXHAUST_OVER_TIME = ApoliRegisters.POWER_FACTORIES.register("exhaust", ExhaustOverTimePower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> FIRE_IMMUNITY = ApoliRegisters.POWER_FACTORIES.register("fire_immunity", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, FireProjectilePower> FIRE_PROJECTILE = ApoliRegisters.POWER_FACTORIES.register("fire_projectile", FireProjectilePower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> IGNORE_WATER = ApoliRegisters.POWER_FACTORIES.register("ignore_water", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, InventoryPower> INVENTORY = ApoliRegisters.POWER_FACTORIES.register("inventory", InventoryPower::new);
	public static final DeferredHolder<PowerFactory<?>, InvisibilityPower> INVISIBILITY = ApoliRegisters.POWER_FACTORIES.register("invisibility", InvisibilityPower::new);
	public static final DeferredHolder<PowerFactory<?>, InvulnerablePower> INVULNERABILITY = ApoliRegisters.POWER_FACTORIES.register("invulnerability", InvulnerablePower::new);
	public static final DeferredHolder<PowerFactory<?>, LaunchPower> LAUNCH = ApoliRegisters.POWER_FACTORIES.register("launch", LaunchPower::new);
	public static final DeferredHolder<PowerFactory<?>, LavaVisionPower> LAVA_VISION = ApoliRegisters.POWER_FACTORIES.register("lava_vision", LavaVisionPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModelColorPower> MODEL_COLOR = ApoliRegisters.POWER_FACTORIES.register("model_color", ModelColorPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValueBlockPower> MODIFY_BREAK_SPEED = ApoliRegisters.POWER_FACTORIES.register("modify_break_speed", ModifyValueBlockPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyDamageDealtPower> MODIFY_DAMAGE_DEALT = ApoliRegisters.POWER_FACTORIES.register("modify_damage_dealt", ModifyDamageDealtPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyDamageTakenPower> MODIFY_DAMAGE_TAKEN = ApoliRegisters.POWER_FACTORIES.register("modify_damage_taken", ModifyDamageTakenPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValuePower> MODIFY_EXHAUSTION = ApoliRegisters.POWER_FACTORIES.register("modify_exhaustion", ModifyValuePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValuePower> MODIFY_EXPERIENCE = ApoliRegisters.POWER_FACTORIES.register("modify_xp_gain", ModifyValuePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyFallingPower> MODIFY_FALLING = ApoliRegisters.POWER_FACTORIES.register("modify_falling", ModifyFallingPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyFoodPower> MODIFY_FOOD = ApoliRegisters.POWER_FACTORIES.register("modify_food", ModifyFoodPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyHarvestPower> MODIFY_HARVEST = ApoliRegisters.POWER_FACTORIES.register("modify_harvest", ModifyHarvestPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyJumpPower> MODIFY_JUMP = ApoliRegisters.POWER_FACTORIES.register("modify_jump", ModifyJumpPower::new);
	public static final DeferredHolder<PowerFactory<?>, AttributeModifierPower> MODIFY_LAVA_SPEED = ApoliRegisters.POWER_FACTORIES.register("modify_lava_speed", () -> new AttributeModifierPower(() -> AdditionalEntityAttributes.LAVA_SPEED));
	public static final DeferredHolder<PowerFactory<?>, ModifyPlayerSpawnPower> MODIFY_PLAYER_SPAWN = ApoliRegisters.POWER_FACTORIES.register("modify_player_spawn", ModifyPlayerSpawnPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyDamageDealtPower> MODIFY_PROJECTILE_DAMAGE = ApoliRegisters.POWER_FACTORIES.register("modify_projectile_damage", ModifyDamageDealtPower::new);
	public static final DeferredHolder<PowerFactory<?>, AttributeModifierPower> MODIFY_SWIM_SPEED = ApoliRegisters.POWER_FACTORIES.register("modify_swim_speed", () -> new AttributeModifierPower(() -> AdditionalEntityAttributes.WATER_SPEED));
	public static final DeferredHolder<PowerFactory<?>, MultiplePower> MULTIPLE = ApoliRegisters.POWER_FACTORIES.register("multiple", MultiplePower::new);
	public static final DeferredHolder<PowerFactory<?>, NightVisionPower> NIGHT_VISION = ApoliRegisters.POWER_FACTORIES.register("night_vision", NightVisionPower::new);
	public static final DeferredHolder<PowerFactory<?>, ParticlePower> PARTICLE = ApoliRegisters.POWER_FACTORIES.register("particle", ParticlePower::new);
	public static final DeferredHolder<PowerFactory<?>, PhasingPower> PHASING = ApoliRegisters.POWER_FACTORIES.register("phasing", PhasingPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventBlockActionPower> PREVENT_BLOCK_SELECTION = ApoliRegisters.POWER_FACTORIES.register("prevent_block_selection", PreventBlockActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventBlockActionPower> PREVENT_BLOCK_USAGE = ApoliRegisters.POWER_FACTORIES.register("prevent_block_use", PreventBlockActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventDeathPower> PREVENT_DEATH = ApoliRegisters.POWER_FACTORIES.register("prevent_death", PreventDeathPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventEntityRenderPower> PREVENT_ENTITY_RENDER = ApoliRegisters.POWER_FACTORIES.register("prevent_entity_render", PreventEntityRenderPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventItemActionPower> PREVENT_ITEM_USAGE = ApoliRegisters.POWER_FACTORIES.register("prevent_item_use", PreventItemActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventSleepPower> PREVENT_SLEEP = ApoliRegisters.POWER_FACTORIES.register("prevent_sleep", PreventSleepPower::new);
	public static final DeferredHolder<PowerFactory<?>, RecipePower> RECIPE = ApoliRegisters.POWER_FACTORIES.register("recipe", RecipePower::new);
	public static final DeferredHolder<PowerFactory<?>, ResourcePower> RESOURCE = ApoliRegisters.POWER_FACTORIES.register("resource", ResourcePower::new);
	public static final DeferredHolder<PowerFactory<?>, RestrictArmorPower> RESTRICT_ARMOR = ApoliRegisters.POWER_FACTORIES.register("restrict_armor", RestrictArmorPower::new);
	public static final DeferredHolder<PowerFactory<?>, SelfCombatActionPower> SELF_ACTION_ON_HIT = ApoliRegisters.POWER_FACTORIES.register("self_action_on_hit", SelfCombatActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, SelfCombatActionPower> SELF_ACTION_ON_KILL = ApoliRegisters.POWER_FACTORIES.register("self_action_on_kill", SelfCombatActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, SelfActionWhenHitPower> SELF_ACTION_WHEN_HIT = ApoliRegisters.POWER_FACTORIES.register("self_action_when_hit", SelfActionWhenHitPower::new);
	public static final DeferredHolder<PowerFactory<?>, ShaderPower> SHADER = ApoliRegisters.POWER_FACTORIES.register("shader", ShaderPower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> SHAKING = ApoliRegisters.POWER_FACTORIES.register("shaking", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> SIMPLE = ApoliRegisters.POWER_FACTORIES.register("simple", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, StackingStatusEffectPower> STACKING_STATUS_EFFECT = ApoliRegisters.POWER_FACTORIES.register("stacking_status_effect", StackingStatusEffectPower::new);
	public static final DeferredHolder<PowerFactory<?>, StartingEquipmentPower> STARTING_EQUIPMENT = ApoliRegisters.POWER_FACTORIES.register("starting_equipment", StartingEquipmentPower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> SWIMMING = ApoliRegisters.POWER_FACTORIES.register("swimming", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, TargetCombatActionPower> TARGET_ACTION_ON_HIT = ApoliRegisters.POWER_FACTORIES.register("target_action_on_hit", TargetCombatActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, TogglePower> TOGGLE = ApoliRegisters.POWER_FACTORIES.register("toggle", TogglePower::new);
	public static final DeferredHolder<PowerFactory<?>, ToggleNightVisionPower> TOGGLE_NIGHT_VISION = ApoliRegisters.POWER_FACTORIES.register("toggle_night_vision", ToggleNightVisionPower::new);
	public static final DeferredHolder<PowerFactory<?>, WalkOnFluidPower> WALK_ON_FLUID = ApoliRegisters.POWER_FACTORIES.register("walk_on_fluid", WalkOnFluidPower::new);
	//region 1.17+ Powers
	public static final DeferredHolder<PowerFactory<?>, EntityGlowPower> SELF_GLOW = ApoliRegisters.POWER_FACTORIES.register("self_glow", () -> new EntityGlowPower(true));
	public static final DeferredHolder<PowerFactory<?>, ModifyValuePower> MODIFY_AIR_SPEED = ApoliRegisters.POWER_FACTORIES.register("modify_air_speed", ModifyValuePower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnEntityUsePower> ACTION_ON_ENTITY_USE = ApoliRegisters.POWER_FACTORIES.register("action_on_entity_use", () -> new ActionOnEntityUsePower(BiEntityInteractionConfiguration.CODEC));
	public static final DeferredHolder<PowerFactory<?>, ActionOnEntityUsePower> PREVENT_ENTITY_USE = ApoliRegisters.POWER_FACTORIES.register("prevent_entity_use", () -> new ActionOnEntityUsePower(BiEntityInteractionConfiguration.PREVENTING_CODEC));
	public static final DeferredHolder<PowerFactory<?>, ActionOnBeingUsedPower> ACTION_ON_BEING_USED = ApoliRegisters.POWER_FACTORIES.register("action_on_being_used", () -> new ActionOnBeingUsedPower(BiEntityInteractionConfiguration.CODEC));
	public static final DeferredHolder<PowerFactory<?>, ActionOnBeingUsedPower> PREVENT_BEING_USED = ApoliRegisters.POWER_FACTORIES.register("prevent_being_used", () -> new ActionOnBeingUsedPower(BiEntityInteractionConfiguration.PREVENTING_CODEC));
	public static final DeferredHolder<PowerFactory<?>, PreventGameEventPower> PREVENT_GAME_EVENT = ApoliRegisters.POWER_FACTORIES.register("prevent_game_event", PreventGameEventPower::new);
	public static final DeferredHolder<PowerFactory<?>, KeepInventoryPower> KEEP_INVENTORY = ApoliRegisters.POWER_FACTORIES.register("keep_inventory", KeepInventoryPower::new);
	public static final DeferredHolder<PowerFactory<?>, SelfActionWhenHitPower> ACTION_WHEN_DAMAGE_TAKEN = ApoliRegisters.POWER_FACTORIES.register("action_when_damage_taken", SelfActionWhenHitPower::new);
	public static final DeferredHolder<PowerFactory<?>, ActionOnBlockUsePower> ACTION_ON_BLOCK_USE = ApoliRegisters.POWER_FACTORIES.register("action_on_block_use", ActionOnBlockUsePower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> FREEZE = ApoliRegisters.POWER_FACTORIES.register("freeze", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, CombatHitActionPower> ACTION_WHEN_HIT = ApoliRegisters.POWER_FACTORIES.register("action_when_hit", CombatHitActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, CombatHitActionPower> ACTION_ON_HIT = ApoliRegisters.POWER_FACTORIES.register("action_on_hit", CombatHitActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, AttributeModifyTransferPower> ATTRIBUTE_MODIFY_TRANSFER = ApoliRegisters.POWER_FACTORIES.register("attribute_modify_transfer", AttributeModifyTransferPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyCameraSubmersionTypePower> MODIFY_CAMERA_SUBMERSION = ApoliRegisters.POWER_FACTORIES.register("modify_camera_submersion", ModifyCameraSubmersionTypePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValueBlockPower> MODIFY_SLIPPERINESS = ApoliRegisters.POWER_FACTORIES.register("modify_slipperiness", ModifyValueBlockPower::new);
	public static final DeferredHolder<PowerFactory<?>, ItemOnItemPower> ITEM_ON_ITEM = ApoliRegisters.POWER_FACTORIES.register("item_on_item", ItemOnItemPower::new);
	public static final DeferredHolder<PowerFactory<?>, TooltipPower> TOOLTIP = ApoliRegisters.POWER_FACTORIES.register("tooltip", TooltipPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyStatusEffectPower> MODIFY_STATUS_EFFECT_AMPLIFIER = ApoliRegisters.POWER_FACTORIES.register("modify_status_effect_amplifier", ModifyStatusEffectPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyStatusEffectPower> MODIFY_STATUS_EFFECT_DURATION = ApoliRegisters.POWER_FACTORIES.register("modify_status_effect_duration", ModifyStatusEffectPower::new);
	public static final DeferredHolder<PowerFactory<?>, BiEntityConditionPower> PREVENT_ENTITY_COLLISION = ApoliRegisters.POWER_FACTORIES.register("prevent_entity_collision", BiEntityConditionPower::new);
	public static final DeferredHolder<PowerFactory<?>, OverlayPower> OVERLAY = ApoliRegisters.POWER_FACTORIES.register("overlay", OverlayPower::new);
	public static final DeferredHolder<PowerFactory<?>, PreventFeatureRenderPower> PREVENT_FEATURE_RENDER = ApoliRegisters.POWER_FACTORIES.register("prevent_feature_render", PreventFeatureRenderPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyCraftingPower> MODIFY_CRAFTING = ApoliRegisters.POWER_FACTORIES.register("modify_crafting", ModifyCraftingPower::new);
	public static final DeferredHolder<PowerFactory<?>, EntityActionPower> PREVENT_ELYTRA_FLIGHT = ApoliRegisters.POWER_FACTORIES.register("prevent_elytra_flight", EntityActionPower::new);
	public static final DeferredHolder<PowerFactory<?>, OverrideHudTexturePower> STATUS_BAR_TEXTURE = ApoliRegisters.POWER_FACTORIES.register("status_bar_texture", OverrideHudTexturePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyBlockRenderPower> MODIFY_BLOCK_RENDER = ApoliRegisters.POWER_FACTORIES.register("modify_block_render", ModifyBlockRenderPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyFluidRenderPower> MODIFY_FLUID_RENDER = ApoliRegisters.POWER_FACTORIES.register("modify_fluid_render", ModifyFluidRenderPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyAttributePower> MODIFY_ATTRIBUTE = ApoliRegisters.POWER_FACTORIES.register("modify_attribute", ModifyAttributePower::new);
	public static final DeferredHolder<PowerFactory<?>, DummyPower> PREVENT_SPRINTING = ApoliRegisters.POWER_FACTORIES.register("prevent_sprinting", DummyPower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValuePower> MODIFY_HEALING = ApoliRegisters.POWER_FACTORIES.register("modify_healing", ModifyValuePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyValuePower> MODIFY_INSONMIA_TICKS = ApoliRegisters.POWER_FACTORIES.register("modify_insomnia_ticks", ModifyValuePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyGrindstonePower> MODIFY_GRINDSTONE = ApoliRegisters.POWER_FACTORIES.register("modify_grindstone", ModifyGrindstonePower::new);
	public static final DeferredHolder<PowerFactory<?>, ReplaceLootTablePower> REPLACE_LOOT_TABLE = ApoliRegisters.POWER_FACTORIES.register("replace_loot_table", ReplaceLootTablePower::new);
	public static final DeferredHolder<PowerFactory<?>, ModifyVelocityPower> MODIFY_VELOCITY = ApoliRegisters.POWER_FACTORIES.register("modify_velocity", ModifyVelocityPower::new);
	public static final DeferredHolder<PowerFactory<?>, GroundedPower> GROUNDED = ApoliRegisters.POWER_FACTORIES.register("grounded", GroundedPower::new);
	//endregion

	public static ConfiguredPower<?, ?> multiple(ImmutableMap<String, ConfiguredPower<?, ?>> powers) {
		return MULTIPLE.value().configure(new MultipleConfiguration<>(Maps.transformValues(powers, Holder::direct)), PowerData.DEFAULT);
	}

	public static void bootstrap() {
	}
}
