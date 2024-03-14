package io.github.edwinmindcraft.apoli.common.registry.action;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.action.block.BonemealAction;
import io.github.apace100.apoli.power.factory.action.block.ExplodeAction;
import io.github.apace100.apoli.power.factory.action.block.ModifyBlockStateAction;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.action.block.*;
import io.github.edwinmindcraft.apoli.common.action.meta.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.BLOCK_ACTIONS;

public class ApoliBlockActions {
	public static final BiConsumer<ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>> EXECUTOR = (action, o) -> action.execute(o.getLeft(), o.getMiddle(), o.getRight());
	public static final BiPredicate<ConfiguredBlockCondition<?, ?>, Triple<Level, BlockPos, Direction>> PREDICATE = (condition, triple) -> condition.check(triple.getLeft(), triple.getMiddle(), () -> triple.getLeft().getBlockState(triple.getMiddle()));
	public static final Predicate<Triple<Level, BlockPos, Direction>> SERVERSIDE_PREDICATE = (block) -> !block.getLeft().isClientSide;

	private static <U extends BlockAction<?>> DeferredHolder<BlockAction<?>, U> of(String name) {
		return DeferredHolder.create(ApoliRegistries.BLOCK_ACTION_KEY.location(), Apoli.identifier(name));
	}

	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<ExecuteMultipleConfiguration<ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> AND = of("and");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<ChanceConfiguration<ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> CHANCE = of("chance");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<IfElseConfiguration<ConfiguredBlockCondition<?, ?>, ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> IF_ELSE = of("if_else");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<IfElseListConfiguration<ConfiguredBlockCondition<?, ?>, ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> IF_ELSE_LIST = of("if_else_list");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<ChoiceConfiguration<ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> CHOICE = of("choice");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<DelayAction<ConfiguredBlockAction<?, ?>, Triple<Level, BlockPos, Direction>>>> DELAY = of("delay");
	public static final DeferredHolder<BlockAction<?>, DelegatedBlockAction<NothingConfiguration<Triple<Level, BlockPos, Direction>>>> NOTHING = of("nothing");

	public static final DeferredHolder<BlockAction<?>, OffsetAction> OFFSET = BLOCK_ACTIONS.register("offset", OffsetAction::new);
	public static final DeferredHolder<BlockAction<?>, SetBlockAction> SET_BLOCK = BLOCK_ACTIONS.register("set_block", SetBlockAction::new);
	public static final DeferredHolder<BlockAction<?>, AddBlockAction> ADD_BLOCK = BLOCK_ACTIONS.register("add_block", AddBlockAction::new);
	public static final DeferredHolder<BlockAction<?>, ExecuteCommandBlockAction> EXECUTE_COMMAND = BLOCK_ACTIONS.register("execute_command", ExecuteCommandBlockAction::new);
	public static final DeferredHolder<BlockAction<?>, BonemealAction> BONEMEAL = BLOCK_ACTIONS.register("bonemeal", BonemealAction::new);
	public static final DeferredHolder<BlockAction<?>, ModifyBlockStateAction> MODIFY_BLOCK_STATE = BLOCK_ACTIONS.register("modify_block_state", ModifyBlockStateAction::new);
	public static final DeferredHolder<BlockAction<?>, ExplodeAction> EXPLODE = BLOCK_ACTIONS.register("explode", ExplodeAction::new);
	public static final DeferredHolder<BlockAction<?>, AreaOfEffectAction> AREA_OF_EFFECT = BLOCK_ACTIONS.register("area_of_effect", AreaOfEffectAction::new);

	public static void bootstrap() {
		MetaFactories.defineMetaActions(BLOCK_ACTIONS, DelegatedBlockAction::new, ConfiguredBlockAction.CODEC_SET, ConfiguredBlockCondition.CODEC_SET, ConfiguredBlockAction::optional, EXECUTOR, PREDICATE, SERVERSIDE_PREDICATE);
	}
}
