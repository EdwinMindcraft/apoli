package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

public class AdvancementAction extends EntityAction<FieldConfiguration<ResourceLocation>> {

    public static AdvancementAction grant() {
        return new AdvancementAction((player, advancement) -> {
            AdvancementProgress advancementProgress = player.getAdvancements().getOrStartProgress(advancement);
            if (!advancementProgress.isDone()) {
                for (String string : advancementProgress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, string);
                }
            }
        });
    }

    public static AdvancementAction revoke() {
        return new AdvancementAction((player, advancement) -> {
            AdvancementProgress advancementProgress = player.getAdvancements().getOrStartProgress(advancement);
            if (advancementProgress.hasProgress()) {
                for (String string : advancementProgress.getCompletedCriteria()) {
                    player.getAdvancements().revoke(advancement, string);
                }
            }
        });
    }

    private final BiConsumer<ServerPlayer, Advancement> action;

    protected AdvancementAction(BiConsumer<ServerPlayer, Advancement> action) {
        super(FieldConfiguration.codec(SerializableDataTypes.IDENTIFIER, "advancement"));
        this.action = action;
    }

    @Override
    public void execute(FieldConfiguration<ResourceLocation> location, Entity entity) {
        if (entity instanceof ServerPlayer player) {
            if (player.getServer() != null) {
                Advancement adv = player.getServer().getAdvancements().getAdvancement(location.value());
                this.action.accept(player, adv);
            }
        }
    }
}
