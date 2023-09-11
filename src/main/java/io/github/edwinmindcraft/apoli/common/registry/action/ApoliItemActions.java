package io.github.edwinmindcraft.apoli.common.registry.action;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.action.item.*;
import io.github.edwinmindcraft.apoli.common.action.meta.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.ITEM_ACTIONS;

public class ApoliItemActions {
	public static final BiConsumer<ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>> EXECUTOR = (action, pair) -> action.execute(pair.getKey(), pair.getValue());
	public static final BiPredicate<ConfiguredItemCondition<?, ?>, Pair<Level, Mutable<ItemStack>>> PREDICATE = (condition, pair) -> condition.check(pair.getKey(), pair.getValue().getValue());
	public static final Predicate<Pair<Level, Mutable<ItemStack>>> SERVERSIDE_PREDICATE = (pair) -> !pair.getLeft().isClientSide;

	private static <U extends ItemAction<?>> RegistryObject<U> of(String name) {
		return RegistryObject.create(Apoli.identifier(name), ApoliRegistries.ITEM_ACTION_KEY.location(), Apoli.MODID);
	}

	public static final RegistryObject<DelegatedItemAction<ExecuteMultipleConfiguration<ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> AND = of("and");
	public static final RegistryObject<DelegatedItemAction<ChanceConfiguration<ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> CHANCE = of("chance");
	public static final RegistryObject<DelegatedItemAction<IfElseConfiguration<ConfiguredItemCondition<?, ?>, ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> IF_ELSE = of("if_else");
	public static final RegistryObject<DelegatedItemAction<IfElseListConfiguration<ConfiguredItemCondition<?, ?>, ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> IF_ELSE_LIST = of("if_else_list");
	public static final RegistryObject<DelegatedItemAction<ChoiceConfiguration<ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> CHOICE = of("choice");
	public static final RegistryObject<DelegatedItemAction<DelayAction<ConfiguredItemAction<?, ?>, Pair<Level, Mutable<ItemStack>>>>> DELAY = of("delay");
	public static final RegistryObject<DelegatedItemAction<NothingConfiguration<Pair<Level, Mutable<ItemStack>>>>> NOTHING = of("nothing");

	public static final RegistryObject<ConsumeItemAction> CONSUME = ITEM_ACTIONS.register("consume", ConsumeItemAction::new);
	public static final RegistryObject<ModifyItemAction> MODIFY = ITEM_ACTIONS.register("modify", ModifyItemAction::new);
	public static final RegistryObject<DamageItemAction> DAMAGE = ITEM_ACTIONS.register("damage", DamageItemAction::new);
	public static final RegistryObject<MergeNbtItemAction> MERGE_NBT = ITEM_ACTIONS.register("merge_nbt", MergeNbtItemAction::new);
	public static final RegistryObject<RemoveEnchantmentItemAction> REMOVE_ENCHANTMENT = ITEM_ACTIONS.register("remove_enchantment", RemoveEnchantmentItemAction::new);
    public static final RegistryObject<HolderAction> HOLDER = ITEM_ACTIONS.register("holder", HolderAction::new);

	public static void bootstrap() {
		MetaFactories.defineMetaActions(ITEM_ACTIONS, DelegatedItemAction::new, ConfiguredItemAction.CODEC_SET, ConfiguredItemCondition.CODEC_SET, ConfiguredItemAction::optional, EXECUTOR, PREDICATE, SERVERSIDE_PREDICATE);
	}
}
