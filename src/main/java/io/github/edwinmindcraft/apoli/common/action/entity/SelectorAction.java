package io.github.edwinmindcraft.apoli.common.action.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.SelectorConfiguration;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class SelectorAction extends EntityAction<SelectorConfiguration> {
    public SelectorAction() {
        super(SelectorConfiguration.CODEC);
    }

    @Override
    public void execute(SelectorConfiguration configuration, Entity entity) {

        MinecraftServer server = entity.getServer();
        if (server == null) return;

        CommandSourceStack source = new CommandSourceStack(
                CommandSource.NULL,
                entity.position(),
                entity.getRotationVector(),
                (ServerLevel) entity.level(),
                2,
                entity.getScoreboardName(),
                entity.getName(),
                server,
                entity
        );

        try {
            configuration.selector().get().findEntities(source)
                    .stream()
                    .filter(e -> ConfiguredBiEntityCondition.check(configuration.biEntityCondition(), entity, e))
                    .forEach(e -> ConfiguredBiEntityAction.execute(configuration.biEntityAction(), entity, e));
        }

        catch (CommandSyntaxException ignored) {}

    }
}
