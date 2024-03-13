package io.github.apace100.apoli.data;

import io.github.apace100.apoli.util.*;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.calio.api.ability.PlayerAbility;
import io.github.edwinmindcraft.calio.api.registry.PlayerAbilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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
			ability -> PlayerAbilities.REGISTRY.get().getKey(ability), id -> {
				ResourceLocation resolvedId = id;
				if (id.getNamespace().equals("minecraft")) {
					resolvedId = new ResourceLocation("calio", id.getPath());
				}
				PlayerAbility ability = PlayerAbilities.REGISTRY.get().getValue(resolvedId);
				if (ability == null) {
					throw new NullPointerException(id + " has not been registered");
				}
				return ability;
			});


    /*public static <T> SerializableDataType<ConditionFactory<T>.Instance> condition(Class<ConditionFactory<T>.Instance> dataClass, ConditionType<T> conditionType) {
        return new SerializableDataType<>(dataClass, conditionType::write, conditionType::read, conditionType::read);
    }

    public static <T> SerializableDataType<ActionFactory<T>.Instance> action(Class<ActionFactory<T>.Instance> dataClass, ActionType<T> actionType) {
        return new SerializableDataType<>(dataClass, actionType::write, actionType::read, actionType::read);
    }*/
}