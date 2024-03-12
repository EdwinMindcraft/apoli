package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class NbtCondition extends BlockCondition<FieldConfiguration<CompoundTag>> {
	public NbtCondition() {
		super(FieldConfiguration.codec(SerializableDataTypes.NBT, "nbt"));
	}

	@Override
	protected boolean check(FieldConfiguration<CompoundTag> configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		CompoundTag nbt = new CompoundTag();
		BlockEntity blockEntity = reader.getBlockEntity(position);
		if (blockEntity != null)
			nbt = blockEntity.serializeNBT();
		return NbtUtils.compareNbt(configuration.value(), nbt, true);
	}
}
