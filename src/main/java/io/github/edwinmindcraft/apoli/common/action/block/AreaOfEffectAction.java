package io.github.edwinmindcraft.apoli.common.action.block;

import io.github.apace100.apoli.action.configuration.AreaOfEffectConfiguration;
import io.github.apace100.apoli.util.Shape;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AreaOfEffectAction extends BlockAction<AreaOfEffectConfiguration<ConfiguredBlockAction<?, ?>, ConfiguredBlockCondition<?, ?>>> {
    public AreaOfEffectAction() {
        super(AreaOfEffectConfiguration.createCodec(ConfiguredBlockAction.required("block_action"), ConfiguredBlockCondition.optional("block_condition")));
    }

    @Override
    public void execute(@NotNull AreaOfEffectConfiguration<ConfiguredBlockAction<?, ?>, ConfiguredBlockCondition<?, ?>> configuration, Level world, BlockPos pos, Direction direction) {
        Shape.forPositions(pos, configuration.shape(), (int)configuration.radius(), blockPos -> {
            if (ConfiguredBlockCondition.check(configuration.condition(), world, blockPos))
                configuration.action().value().execute(world, blockPos, direction);
        }, () -> false);
    }
}
