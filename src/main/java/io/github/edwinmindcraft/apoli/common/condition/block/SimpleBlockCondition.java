package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;

public class SimpleBlockCondition extends BlockCondition<NoConfiguration> {

	public static final BlockPredicate REPLACEABLE = (reader, pos, stateGetter) -> stateGetter.get().getMaterial().isReplaceable();
	public static final BlockPredicate MOVEMENT_BLOCKING = (reader, pos, stateGetter) -> stateGetter.get().getMaterial().blocksMotion() && !stateGetter.get().getCollisionShape(reader, pos).isEmpty();
	public static final BlockPredicate LIGHT_BLOCKING = (reader, pos, stateGetter) -> stateGetter.get().getMaterial().isSolidBlocking();
	public static final BlockPredicate WATER_LOGGABLE = (reader, pos, stateGetter) -> stateGetter.get().getBlock() instanceof LiquidBlockContainer;
	public static final BlockPredicate EXPOSED_TO_SKY = (reader, pos, stateGetter) -> reader.canSeeSky(pos);
	public static final BlockPredicate BLOCK_ENTITY = (reader, pos, stateGetter) -> reader.getBlockEntity(pos) != null;

	private final BlockPredicate blockPredicate;

	public SimpleBlockCondition(BlockPredicate blockPredicate) {
		super(NoConfiguration.CODEC);
		this.blockPredicate = blockPredicate;
	}

	@Override
	protected boolean check(NoConfiguration configuration, LevelReader reader, BlockPos position, NonNullSupplier<BlockState> stateGetter) {
		return this.blockPredicate.test(reader, position, stateGetter);
	}
}
