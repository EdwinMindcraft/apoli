package io.github.edwinmindcraft.apoli.common.registry.action;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.bientity.DamageAction;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.action.bientity.*;
import io.github.edwinmindcraft.apoli.common.action.meta.*;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.BIENTITY_ACTIONS;

public class ApoliBiEntityActions {
	public static final BiConsumer<ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>> EXECUTOR = (action, pair) -> action.execute(pair.getKey(), pair.getValue());
	public static final BiPredicate<ConfiguredBiEntityCondition<?, ?>, Pair<Entity, Entity>> PREDICATE = (condition, pair) -> condition.check(pair.getKey(), pair.getValue());
	public static final Predicate<Pair<Entity, Entity>> SERVERSIDE_PREDICATE = (pair) -> !pair.getLeft().level().isClientSide;

	private static <U extends BiEntityAction<?>> DeferredHolder<BiEntityAction<?>,U> of(String name) {
		return DeferredHolder.create(ApoliRegistries.BIENTITY_ACTION_KEY.location(), Apoli.identifier(name));
	}

	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<ExecuteMultipleConfiguration<ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> AND = of("and");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<ChanceConfiguration<ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> CHANCE = of("chance");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<IfElseConfiguration<ConfiguredBiEntityCondition<?, ?>, ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> IF_ELSE = of("if_else");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<IfElseListConfiguration<ConfiguredBiEntityCondition<?, ?>, ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> IF_ELSE_LIST = of("if_else_list");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<ChoiceConfiguration<ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> CHOICE = of("choice");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<DelayAction<ConfiguredBiEntityAction<?, ?>, Pair<Entity, Entity>>>> DELAY = of("delay");
	public static final DeferredHolder<BiEntityAction<?>,DelegatedBiEntityAction<NothingConfiguration<Pair<Entity, Entity>>>> NOTHING = of("nothing");

	public static final DeferredHolder<BiEntityAction<?>,InvertBiEntityAction> INVERT = BIENTITY_ACTIONS.register("invert", InvertBiEntityAction::new);
	public static final DeferredHolder<BiEntityAction<?>,DispatchBiEntityAction> ACTOR_ACTION = BIENTITY_ACTIONS.register("actor_action", DispatchBiEntityAction::actor);
	public static final DeferredHolder<BiEntityAction<?>,DispatchBiEntityAction> TARGET_ACTION = BIENTITY_ACTIONS.register("target_action", DispatchBiEntityAction::target);
	public static final DeferredHolder<BiEntityAction<?>,SimpleBiEntityAction> MOUNT = BIENTITY_ACTIONS.register("mount", () -> new SimpleBiEntityAction(SimpleBiEntityAction::mount));
	public static final DeferredHolder<BiEntityAction<?>,SimpleBiEntityAction> SET_IN_LOVE = BIENTITY_ACTIONS.register("set_in_love", () -> new SimpleBiEntityAction(SimpleBiEntityAction::setInLove));
	public static final DeferredHolder<BiEntityAction<?>,SimpleBiEntityAction> TAME = BIENTITY_ACTIONS.register("tame", () -> new SimpleBiEntityAction(SimpleBiEntityAction::tame));
	public static final DeferredHolder<BiEntityAction<?>,AddVelocityAction> ADD_VELOCITY = BIENTITY_ACTIONS.register("add_velocity", AddVelocityAction::new);
	public static final DeferredHolder<BiEntityAction<?>,DamageAction> DAMAGE = BIENTITY_ACTIONS.register("damage", DamageAction::new);


	public static void bootstrap() {
		MetaFactories.defineMetaActions(BIENTITY_ACTIONS, DelegatedBiEntityAction::new, ConfiguredBiEntityAction.CODEC_SET, ConfiguredBiEntityCondition.CODEC_SET, ConfiguredBiEntityAction::optional, EXECUTOR, PREDICATE, SERVERSIDE_PREDICATE);
	}
}
