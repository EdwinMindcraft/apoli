package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyGrindstoneConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Optional;

public class ModifyGrindstonePower extends PowerFactory<ModifyGrindstoneConfiguration> {

    public static void tryLateExecute(List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> powers, Entity self, Mutable<ItemStack> itemStack, Optional<BlockPos> pos) {
        for (Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>> holder : powers) {
            holder.value().getConfiguration().tryExecute(holder.value(), self, itemStack, pos);
        }
    }

    public static List<ConfiguredModifier<?>> tryGetExperienceModifiers(List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> powers) {
        return powers.stream().flatMap(holder -> holder.value().getConfiguration().experienceModifier().stream()).toList();
    }

    public static List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> tryGetApplyingPowers(Entity entity, ItemStack top, ItemStack bottom, ItemStack original, Optional<BlockPos> pos) {
        return PowerContainer.getPowers(entity, ApoliPowers.MODIFY_GRINDSTONE.get()).stream().filter(holder -> holder.value().getConfiguration().doesApply(holder.value(), entity.level(), top, bottom, original, pos)).toList();
    }

    public static ItemStack tryCreateOutput(List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> holders, Level level, ItemStack top, ItemStack bottom, ItemStack currentOutput) {
        Mutable<ItemStack> output = new MutableObject<>(currentOutput.copy());

        for (Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>> holder : holders) {
            ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower> power = holder.value();
            switch (power.getConfiguration().resultType()) {
                case SPECIFIED -> power.getConfiguration().resultStack().ifPresent(resultStack -> output.setValue(resultStack.copy()));
                case FROM_BOTTOM -> output.setValue(bottom.copy());
                case FROM_TOP -> output.setValue(top.copy());
            }
            ConfiguredItemAction.execute(power.getConfiguration().resultItemAction(), level, output);
        }
        return output.getValue();
    }

    public ModifyGrindstonePower() {
        super(ModifyGrindstoneConfiguration.CODEC);
    }


    public enum ResultType {
        UNCHANGED, SPECIFIED, FROM_TOP, FROM_BOTTOM
    }
}
