package io.github.apace100.apoli.data;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.*;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.ArgumentWrapper;
import io.github.apace100.apoli.util.ArmPoseReference;
import io.github.apace100.apoli.util.BlockUsagePhase;
import io.github.apace100.apoli.util.StackClickPhase;
import io.github.edwinmindcraft.calio.api.ability.PlayerAbility;
import io.github.edwinmindcraft.calio.api.registry.PlayerAbilities;
import io.github.edwinmindcraft.calio.common.util.DynamicIdentifier;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//FIXME Reintroduce
public class ApoliDataTypes {

    /*public static final SerializableDataType<PowerTypeReference> POWER_TYPE = SerializableDataType.wrap(
        PowerTypeReference.class, SerializableDataTypes.IDENTIFIER,
        PowerType::getIdentifier, PowerTypeReference::new);

    public static final SerializableDataType<ConditionFactory<Entity>.Instance> ENTITY_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.ENTITY);

    public static final SerializableDataType<List<ConditionFactory<Entity>.Instance>> ENTITY_CONDITIONS =
        SerializableDataType.list(ENTITY_CONDITION);

	public static final SerializableDataType<ConditionFactory<Pair<Entity, Entity>>.Instance> BIENTITY_CONDITION =
			condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.BIENTITY);

	public static final SerializableDataType<List<ConditionFactory<Pair<Entity, Entity>>.Instance>> BIENTITY_CONDITIONS =
			SerializableDataType.list(BIENTITY_CONDITION);

    public static final SerializableDataType<ConditionFactory<ItemStack>.Instance> ITEM_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.ITEM);

    public static final SerializableDataType<List<ConditionFactory<ItemStack>.Instance>> ITEM_CONDITIONS =
        SerializableDataType.list(ITEM_CONDITION);

    public static final SerializableDataType<ConditionFactory<BlockInWorld>.Instance> BLOCK_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.BLOCK);

    public static final SerializableDataType<List<ConditionFactory<BlockInWorld>.Instance>> BLOCK_CONDITIONS =
        SerializableDataType.list(BLOCK_CONDITION);

    public static final SerializableDataType<ConditionFactory<FluidState>.Instance> FLUID_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.FLUID);

    public static final SerializableDataType<List<ConditionFactory<FluidState>.Instance>> FLUID_CONDITIONS =
        SerializableDataType.list(FLUID_CONDITION);

    public static final SerializableDataType<ConditionFactory<Tuple<DamageSource, Float>>.Instance> DAMAGE_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.DAMAGE);

    public static final SerializableDataType<List<ConditionFactory<Tuple<DamageSource, Float>>.Instance>> DAMAGE_CONDITIONS =
        SerializableDataType.list(DAMAGE_CONDITION);

    public static final SerializableDataType<ConditionFactory<Biome>.Instance> BIOME_CONDITION =
        condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.BIOME);

    public static final SerializableDataType<List<ConditionFactory<Biome>.Instance>> BIOME_CONDITIONS =
        SerializableDataType.list(BIOME_CONDITION);

    public static final SerializableDataType<ActionFactory<Entity>.Instance> ENTITY_ACTION =
        action(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.ENTITY);

    public static final SerializableDataType<List<ActionFactory<Entity>.Instance>> ENTITY_ACTIONS =
        SerializableDataType.list(ENTITY_ACTION);

	public static final SerializableDataType<ActionFactory<Tuple<Entity, Entity>>.Instance> BIENTITY_ACTION =
			action(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.BIENTITY);

	public static final SerializableDataType<List<ActionFactory<Tuple<Entity, Entity>>.Instance>> BIENTITY_ACTIONS =
			SerializableDataType.list(BIENTITY_ACTION);

    public static final SerializableDataType<ActionFactory<Triple<Level, BlockPos, Direction>>.Instance> BLOCK_ACTION =
        action(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.BLOCK);

    public static final SerializableDataType<List<ActionFactory<Triple<Level, BlockPos, Direction>>.Instance>> BLOCK_ACTIONS =
        SerializableDataType.list(BLOCK_ACTION);

    public static final SerializableDataType<ActionFactory<Pair<World, ItemStack>>.Instance> ITEM_ACTION =
        action(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.ITEM);

    public static final SerializableDataType<List<ActionFactory<Pair<World, ItemStack>>.Instance>> ITEM_ACTIONS =
        SerializableDataType.list(ITEM_ACTION);*/

	public static final SerializableDataType<Space> SPACE = SerializableDataType.enumValue(Space.class);

	public static final SerializableDataType<ResourceOperation> RESOURCE_OPERATION = SerializableDataType.enumValue(ResourceOperation.class);

    public static final SerializableDataType<InventoryUtil.InventoryType> INVENTORY_TYPE = SerializableDataType.enumValue(InventoryUtil.InventoryType.class);

    public static final SerializableDataType<EnumSet<InventoryUtil.InventoryType>> INVENTORY_TYPE_SET = SerializableDataType.enumSet(InventoryUtil.InventoryType.class, INVENTORY_TYPE);

    public static final SerializableDataType<InventoryUtil.ProcessMode> PROCESS_MODE = SerializableDataType.enumValue(InventoryUtil.ProcessMode.class);

	public static final SerializableDataType<AttributedEntityAttributeModifier> ATTRIBUTED_ATTRIBUTE_MODIFIER = new SerializableDataType<>(AttributedEntityAttributeModifier.class, AttributedEntityAttributeModifier.CODEC);

	public static final SerializableDataType<List<AttributedEntityAttributeModifier>> ATTRIBUTED_ATTRIBUTE_MODIFIERS = SerializableDataType.list(ATTRIBUTED_ATTRIBUTE_MODIFIER);

	public static final SerializableDataType<Tuple<Integer, ItemStack>> POSITIONED_ITEM_STACK = SerializableDataType.compound(ClassUtil.castClass(Tuple.class),
			new SerializableData()
					.add("item", SerializableDataTypes.ITEM)
					.add("amount", SerializableDataTypes.INT, 1)
					.add("tag", SerializableDataTypes.NBT, null)
					.add("slot", SerializableDataTypes.INT, Integer.MIN_VALUE),
			(data) -> {
				ItemStack stack = new ItemStack((Item) data.get("item"), data.getInt("amount"));
				if (data.isPresent("tag")) {
					stack.setTag(data.get("tag"));
				}
				return new Tuple<>(data.getInt("slot"), stack);
			},
			((serializableData, positionedStack) -> {
				SerializableData.Instance data = serializableData.new Instance();
				data.set("item", positionedStack.getB().getItem());
				data.set("amount", positionedStack.getB().getCount());
				data.set("tag", positionedStack.getB().hasTag() ? positionedStack.getB().getTag() : null);
				data.set("slot", positionedStack.getA());
				return data;
			}));

	public static final SerializableDataType<List<Tuple<Integer, ItemStack>>> POSITIONED_ITEM_STACKS = SerializableDataType.list(POSITIONED_ITEM_STACK);

   /* public static final SerializableDataType<Active.Key> KEY = SerializableDataType.compound(Active.Key.class,
        new SerializableData()
            .add("key", SerializableDataTypes.STRING)
            .add("continuous", SerializableDataTypes.BOOLEAN, false),
        (data) ->  {
            Active.Key key = new Active.Key();
            key.key = data.getString("key");
            key.continuous = data.getBoolean("continuous");
            return key;
        },
        ((serializableData, key) -> {
            SerializableData.Instance data = serializableData.new Instance();
            data.set("key", key.key);
            data.set("continuous", key.continuous);
            return data;
        }));

    public static final SerializableDataType<Active.Key> BACKWARDS_COMPATIBLE_KEY = new SerializableDataType<>(Active.Key.class,
        KEY::send, KEY::receive, jsonElement -> {
        if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            String keyString = jsonElement.getAsString();
            Active.Key key = new Active.Key();
            key.key = keyString;
            key.continuous = false;
            return key;
        }
        return KEY.read(jsonElement);
    });*/

	public static final SerializableDataType<HudRender> HUD_RENDER = new SerializableDataType<>(HudRender.class, HudRender.CODEC);

	public static final SerializableDataType<Comparison> COMPARISON = SerializableDataType.enumValue(Comparison.class,
			SerializationHelper.buildEnumMap(Comparison.class, Comparison::getComparisonString));

	public static final SerializableDataType<PlayerAbility> PLAYER_ABILITY = SerializableDataType.wrap(
			PlayerAbility.class, SerializableDataTypes.IDENTIFIER,
			ability -> PlayerAbilities.REGISTRY.getKey(ability), id -> {
                ResourceLocation resolvedId = id;
                if (id.getNamespace().equals("minecraft")) {
                    resolvedId = new ResourceLocation("calio", id.getPath());
                }
                PlayerAbility ability = PlayerAbilities.REGISTRY.get(resolvedId);
                if (ability == null) {
                    throw new NullPointerException(id + " has not been registered");
                }
                return ability;
            });

	public static final SerializableDataType<ArgumentWrapper<Integer>> ITEM_SLOT = SerializableDataType.argumentType(SlotArgument.slot());

	public static final SerializableDataType<List<ArgumentWrapper<Integer>>> ITEM_SLOTS = SerializableDataType.list(ITEM_SLOT);

    public static final SerializableDataType<Explosion.BlockInteraction> BACKWARDS_COMPATIBLE_DESTRUCTION_TYPE = SerializableDataType.mapped(Explosion.BlockInteraction.class,
            HashBiMap.create(ImmutableBiMap.of(
                    "none", Explosion.BlockInteraction.KEEP,
                    "break", Explosion.BlockInteraction.DESTROY,
                    "destroy", Explosion.BlockInteraction.DESTROY_WITH_DECAY)
            ));

    public static final SerializableDataType<ArgumentWrapper<EntitySelector>> ENTITIES_SELECTOR = SerializableDataType.argumentType(EntityArgument.entities());

    public static final SerializableDataType<DamageSourceDescription> DAMAGE_SOURCE_DESCRIPTION = new SerializableDataType<>(DamageSourceDescription.class, DamageSourceDescription.CODEC);

    public static final SerializableDataType<LegacyMaterial> LEGACY_MATERIAL = SerializableDataType.wrap(
            LegacyMaterial.class, SerializableDataTypes.STRING,
            LegacyMaterial::getMaterial, LegacyMaterial::new
    );

    public static final SerializableDataType<List<LegacyMaterial>> LEGACY_MATERIALS = SerializableDataType.list(LEGACY_MATERIAL);

	public static final SerializableDataType<ClickAction> CLICK_TYPE = SerializableDataType.enumValue(ClickAction.class);

	public static final SerializableDataType<EnumSet<ClickAction>> CLICK_TYPE_SET = SerializableDataType.enumSet(ClickAction.class, CLICK_TYPE);

	public static final SerializableDataType<Display.TextDisplay.Align> TEXT_ALIGNMENT = SerializableDataType.enumValue(Display.TextDisplay.Align.class);

	public static final SerializableDataType<Map<ResourceLocation, ResourceLocation>> IDENTIFIER_MAP = new SerializableDataType<>(
			ClassUtil.castClass(Map.class),
			(buffer, idMap) -> buffer.writeMap(
					idMap,
					FriendlyByteBuf::writeResourceLocation,
					FriendlyByteBuf::writeResourceLocation
			),
			buffer -> buffer.readMap(
					FriendlyByteBuf::readResourceLocation,
					FriendlyByteBuf::readResourceLocation
			),
			jsonElement -> {

				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonParseException("Expected a JSON object");
				}

				Map<ResourceLocation, ResourceLocation> map = new LinkedHashMap<>();
				for (String key : jsonObject.keySet()) {

					if (!(jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
						continue;
					}

					ResourceLocation keyId = DynamicIdentifier.of(key);
					ResourceLocation valId = DynamicIdentifier.of(jsonPrimitive.getAsString());

					map.put(keyId, valId);

				}

				return map;

			},
			idMap -> {

				JsonObject jsonObject = new JsonObject();
				idMap.forEach((keyId, valId) -> jsonObject.addProperty(keyId.toString(), valId.toString()));

				return jsonObject;

			}
	);

	public static final SerializableDataType<Map<Pattern, ResourceLocation>> REGEX_MAP = new SerializableDataType<>(
			ClassUtil.castClass(Map.class),
			(buffer, regexMap) -> buffer.writeMap(
					regexMap,
					(keyBuffer, pattern) -> keyBuffer.writeUtf(pattern.toString()),
					FriendlyByteBuf::writeResourceLocation
			),
			buffer -> buffer.readMap(
					keyBuffer -> Pattern.compile(keyBuffer.readUtf()),
					FriendlyByteBuf::readResourceLocation
			),
			jsonElement -> {

				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				Map<Pattern, ResourceLocation> regexMap = new HashMap<>();
				for (String key : jsonObject.keySet()) {

					if (!(jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
						continue;
					}

					Pattern pattern = Pattern.compile(key);
					ResourceLocation id = DynamicIdentifier.of(jsonPrimitive);

					regexMap.put(pattern, id);

				}

				return regexMap;

			},
			regexMap -> {

				JsonObject jsonObject = new JsonObject();
				regexMap.forEach((regex, id) -> jsonObject.addProperty(regex.pattern(), id.toString()));

				return jsonObject;

			}
	);

	public static final SerializableDataType<GameType> GAME_MODE = SerializableDataType.enumValue(GameType.class);

	//  This is for keeping backwards compatibility to fields that used to accept strings as translation keys
	public static final SerializableDataType<Component> DEFAULT_TRANSLATABLE_TEXT = new SerializableDataType<>(
			ClassUtil.castClass(Component.class),
			ComponentSerialization.TRUSTED_STREAM_CODEC::encode,
			ComponentSerialization.TRUSTED_STREAM_CODEC::decode,
			jsonElement -> jsonElement instanceof JsonPrimitive jsonPrimitive
					? Component.translatable(jsonPrimitive.getAsString())
					: SerializableDataTypes.TEXT.read(jsonElement),
			text -> ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, text)
					.mapError(err -> "Failed to serialize text to JSON (skipping): " + err)
					.resultOrPartial(Apoli.LOGGER::warn)
					.orElseGet(JsonObject::new)
	);

	public static final SerializableDataType<Integer> NON_NEGATIVE_INT = SerializableDataType.boundNumber(
			SerializableDataTypes.INT, 0, Integer.MAX_VALUE,
			value -> (min, max) -> {

				if (value < min) {
					throw new IllegalArgumentException("Expected value to be equal or greater than " + min + "! (current value: " + value + ")");
				}

				return value;

			}
	);

	public static final SerializableDataType<StackClickPhase> STACK_CLICK_PHASE = SerializableDataType.enumValue(StackClickPhase.class);

	public static final SerializableDataType<EnumSet<StackClickPhase>> STACK_CLICK_PHASE_SET = SerializableDataType.enumSet(StackClickPhase.class, STACK_CLICK_PHASE);

	public static final SerializableDataType<BlockUsagePhase> BLOCK_USAGE_PHASE = SerializableDataType.enumValue(BlockUsagePhase.class);

	public static final SerializableDataType<EnumSet<BlockUsagePhase>> BLOCK_USAGE_PHASE_SET = SerializableDataType.enumSet(BlockUsagePhase.class, BLOCK_USAGE_PHASE);

	public static final SerializableDataType<Pose> ENTITY_POSE = SerializableDataType.enumValue(Pose.class);

	public static final SerializableDataType<ArmPoseReference> ARM_POSE_REFERENCE = SerializableDataType.enumValue(ArmPoseReference.class);


	public static final SerializableDataType<Stat<?>> STAT = SerializableDataType.compound(ClassUtil.castClass(Stat.class),
			new SerializableData()
					.add("type", SerializableDataType.registry(ClassUtil.castClass(StatType.class), BuiltInRegistries.STAT_TYPE))
					.add("id", SerializableDataTypes.IDENTIFIER),
			data -> {
				StatType statType = data.get("type");
				Registry<?> statRegistry = statType.getRegistry();
				ResourceLocation statId = data.get("id");
				if(statRegistry.containsKey(statId)) {
					Object statObject = statRegistry.get(statId);
					return statType.get(statObject);
				}
				throw new IllegalArgumentException("Desired stat \"" + statId + "\" does not exist in stat type ");
			},
			(data, stat) -> {
				SerializableData.Instance inst = data.new Instance();
				inst.set("type", stat.getType());
				Registry reg = stat.getType().getRegistry();
				ResourceLocation statId = reg.getKey(stat.getValue());
				inst.set("id", statId);
				return inst;
			});

    /*public static <T> SerializableDataType<ConditionFactory<T>.Instance> condition(Class<ConditionFactory<T>.Instance> dataClass, ConditionType<T> conditionType) {
        return new SerializableDataType<>(dataClass, conditionType::write, conditionType::read, conditionType::read);
    }

    public static <T> SerializableDataType<ActionFactory<T>.Instance> action(Class<ActionFactory<T>.Instance> dataClass, ActionType<T> actionType) {
        return new SerializableDataType<>(dataClass, actionType::write, actionType::read, actionType::read);
    }*/
}