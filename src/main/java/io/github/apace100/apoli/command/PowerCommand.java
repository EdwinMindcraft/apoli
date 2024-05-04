package io.github.apace100.apoli.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.util.JsonTextFormatter;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import joptsimple.internal.Strings;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PowerCommand {

	public static final ResourceLocation POWER_SOURCE = Apoli.identifier("command");

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
                literal("power").requires(scs -> scs.hasPermission(2))
                        .then(literal("grant")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("power", PowerTypeArgumentType.power())
                                                .executes(context -> grantPower(context, false))
                                                .then(argument("source", PowerSourceArgumentType.powerSource("targets"))
                                                        .executes(context -> grantPower(context, true)))))
                        )
                        .then(literal("revoke")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("power", PowerTypeArgumentType.power())
                                                .executes(context -> revokePower(context, false))
                                                .then(argument("source", PowerSourceArgumentType.powerSource("targets"))
                                                        .executes(context -> revokePower(context, true)))))
                        )
                        .then(literal("revokeall")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("source", PowerSourceArgumentType.powerSource("targets"))
                                                .executes(PowerCommand::revokeAllPowers)))
                        )
                        .then(literal("list")
                                .then(argument("target", EntityArgument.entities())
                                        .executes(context -> listPowers(context, false))
                                        .then(argument("subpowers", BoolArgumentType.bool())
                                                .executes(context -> listPowers(context, true))))
                        )
                        .then(literal("has")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("power", PowerTypeArgumentType.power())
                                                .executes(PowerCommand::hasPower)))
                        )
                        .then(literal("sources")
                                .then(argument("target", EntityArgument.entities())
                                        .then(argument("power", PowerTypeArgumentType.power())
                                                .executes(PowerCommand::getSourcesFromPower)))
                        )
                        .then(literal("remove")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("power", PowerTypeArgumentType.power())
                                                .executes(PowerCommand::removePower)))
                        )
                        .then(literal("clear")
                                .executes(context -> clearAllPowers(context, true))
                                .then(argument("targets", EntityArgument.entities())
                                        .executes(context -> clearAllPowers(context, false)))
                        )
						.then(literal("dump")
								.then(argument("power", PowerTypeArgumentType.power())
                                        .executes(context -> dumpPower(context, false))
                                        .then(argument("indent", IntegerArgumentType.integer(0))
                                                .executes(context -> dumpPower(context, true))))
                        )
						.then(literal("condition")
								.then(argument("target", EntityArgument.entity())
										.then(argument("condition_json", EntityConditionArgument.entityCondition())
                                                .executes(PowerCommand::checkCondition)))
                        )
        );
	}

    private static int grantPower(CommandContext<CommandSourceStack> context, boolean isSourceSpecified) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ResourceKey<ConfiguredPower<?, ?>> power = PowerTypeArgumentType.getConfiguredPower(context, "power");
        ResourceLocation powerSource = isSourceSpecified ? ResourceLocationArgument.getId(context, "source") : Apoli.identifier("command");

        LinkedList<Entity> nonLivingTargets = new LinkedList<>();
        LinkedList<LivingEntity> livingTargets = new LinkedList<>();
        LinkedList<LivingEntity> processedLivingTargets = new LinkedList<>();

        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget)) {
                nonLivingTargets.add(target);
                continue;
            }

            livingTargets.add(livingTarget);

            if (!grantPower(livingTarget, power, powerSource)) {
                continue;
            }

            processedLivingTargets.add(livingTarget);

        }

        if (!processedLivingTargets.isEmpty()) {
            if (isSourceSpecified) {
                if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.grant.success.single", processedLivingTargets.getFirst().getDisplayName(), getPowerName(power, source)), true);
                else source.sendSuccess(() -> Component.translatable("commands.apoli.grant.success.multiple", processedLivingTargets.size(), getPowerName(power, source)), true);
            }
            else {
                if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.grant_from_source.success.single", processedLivingTargets.getFirst().getDisplayName(), getPowerName(power, source), powerSource), true);
                else source.sendSuccess(() -> Component.translatable("commands.apoli.grant_from_source.success.multiple", processedLivingTargets.size(), getPowerName(power, source), powerSource), true);
            }
        }

        else if (!livingTargets.isEmpty()) {
            if (livingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.grant.fail.single", livingTargets.getFirst().getDisplayName(), getPowerName(power, source), powerSource));
            else source.sendFailure(Component.translatable("commands.apoli.grant.fail.multiple", livingTargets.size(), getPowerName(power, source), powerSource));
        }

        else if (!nonLivingTargets.isEmpty()) {
            if (nonLivingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.grant.invalid_entity", nonLivingTargets.getFirst().getDisplayName()));
            else source.sendFailure(Component.translatable("commands.apoli.grant.invalid_entities", nonLivingTargets.size()));
        }

        return processedLivingTargets.size();

    }

	private static boolean grantPower(LivingEntity entity, ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation source) {
		return IPowerContainer.get(entity).map(component -> {
			boolean success = component.addPower(power, source);
			if (success)
				component.sync();
			return success;
		}).orElse(false);
	}

    private static int revokePower(CommandContext<CommandSourceStack> context, boolean isSourceSpecified) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ResourceKey<ConfiguredPower<?, ?>> power = PowerTypeArgumentType.getConfiguredPower(context, "power");
        ResourceLocation powerSource = isSourceSpecified ? ResourceLocationArgument.getId(context, "source") : POWER_SOURCE;

        LinkedList<Entity> nonLivingTargets = new LinkedList<>();
        LinkedList<LivingEntity> livingTargets = new LinkedList<>();
        LinkedList<LivingEntity> processedLivingTargets = new LinkedList<>();

        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget)) {
                nonLivingTargets.add(target);
                continue;
            }

            livingTargets.add(livingTarget);

            if (!hasPower(livingTarget, power, powerSource) || !revokePower(livingTarget, power, powerSource)) {
                continue;
            }

            processedLivingTargets.add(livingTarget);

        }

        if (!processedLivingTargets.isEmpty()) {
            if (!isSourceSpecified) {
                if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.revoke.success.single", processedLivingTargets.getFirst().getDisplayName(), getPowerName(power, source)), true);
                else source.sendSuccess(() -> Component.translatable("commands.apoli.revoke.success.multiple", processedLivingTargets.size(), getPowerName(power, source)), true);
            }
            else {
                if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.revoke_from_source.success.single", processedLivingTargets.getFirst().getDisplayName(), getPowerName(power, source), powerSource), true);
                else source.sendSuccess(() -> Component.translatable("commands.apoli.revoke_from_source.success.multiple", processedLivingTargets.size(), getPowerName(power, source), powerSource), true);
            }
        }

        else if (!livingTargets.isEmpty()) {
            if (livingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.revoke.fail.single", livingTargets.getFirst().getDisplayName(), getPowerName(power, source), powerSource));
            else source.sendFailure(Component.translatable("commands.apoli.revoke.fail.multiple", getPowerName(power, source), powerSource));
        }

        else if (!nonLivingTargets.isEmpty()) {
            if (nonLivingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.revoke.invalid_entity", nonLivingTargets.getFirst().getDisplayName(), powerSource));
            else source.sendFailure(Component.translatable("commands.apoli.revoke.invalid_entities", nonLivingTargets.size(), powerSource));
        }

        return processedLivingTargets.size();

    }


	private static boolean revokePower(LivingEntity entity, ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation source) {
		return IPowerContainer.get(entity).map(component -> {
			if (component.hasPower(power, source)) {
				component.removePower(power, source);
				component.sync();
				return true;
			}
			return false;
		}).orElse(false);
	}

    private static int revokeAllPowers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();

        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ResourceLocation powerSource = ResourceLocationArgument.getId(context, "source");
        int revokedPowers = 0;

        LinkedList<Entity> nonLivingTargets = new LinkedList<>();
        LinkedList<LivingEntity> livingTargets = new LinkedList<>();
        LinkedList<LivingEntity> processedLivingTargets = new LinkedList<>();

        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget)) {
                nonLivingTargets.add(target);
                continue;
            }

            livingTargets.add(livingTarget);
            int i = revokeAllPowersFromSource(livingTarget, powerSource);

            if (i == 0) {
                continue;
            }

            revokedPowers += i;
            processedLivingTargets.add(livingTarget);
        }

        if (!processedLivingTargets.isEmpty()) {
            final int currentRevokedPowers = revokedPowers;
            if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.revoke_all.success.single", processedLivingTargets.getFirst().getDisplayName(), currentRevokedPowers, powerSource), true);
            else source.sendSuccess(() -> Component.translatable("commands.apoli.revoke_all.success.multiple", processedLivingTargets.size(), currentRevokedPowers, powerSource), true);
        }

        else if (!livingTargets.isEmpty()) {
            if (livingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.revoke_all.fail.single", livingTargets.getFirst().getDisplayName(), powerSource));
            else source.sendFailure(Component.translatable("commands.apoli.revoke_all.fail.multiple", powerSource));
        }

        else if (!nonLivingTargets.isEmpty()) {
            if (nonLivingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.revoke_all.invalid_entity", nonLivingTargets.getFirst().getDisplayName(), powerSource));
            else source.sendFailure(Component.translatable("commands.apoli.revoke_all.invalid_entities", nonLivingTargets.size(), powerSource));
        }

        return processedLivingTargets.size();

    }

    private static int revokeAllPowersFromSource(LivingEntity entity, ResourceLocation source) {
        return IPowerContainer.get(entity).map(component -> {
            int i = component.removeAllPowersFromSource(source);
            if (i > 0) {
                component.sync();
            }
            return i;
        }).orElse(0);
    }

    private static int listPowers(CommandContext<CommandSourceStack> context, boolean includeSubpowers) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");
        List<Component> powers = new LinkedList<>();
        int powerCount  = 0;

        if (!(target instanceof LivingEntity livingTarget)) {
            source.sendFailure(Component.translatable("commands.apoli.list.fail", target.getDisplayName()));
            return powerCount;
        }

        IPowerContainer component = ApoliAPI.getPowerContainer(livingTarget);
        if (component == null) {
            source.sendFailure(Component.translatable("commands.apoli.list.fail", target.getDisplayName()));
            return powerCount;
        }

        for (ResourceKey<ConfiguredPower<?, ?>> power : component.getPowerTypes(includeSubpowers)) {

            List<Component> sourcesTooltip = new LinkedList<>();
            component.getSources(power).forEach(id -> sourcesTooltip.add(Component.literal(id.toString())));

            HoverEvent sourceHoverEvent = new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("commands.apoli.list.sources", ComponentUtils.formatList(sourcesTooltip, Component.literal(",")))
            );

            Component powerTooltip = Component.literal(power.location().toString())
                    .setStyle(Style.EMPTY.withHoverEvent(sourceHoverEvent));

            powers.add(powerTooltip);
            powerCount++;

        }

        if (powerCount == 0) {
            source.sendFailure(Component.translatable("commands.apoli.list.fail", target.getName()));
        } else {
            int finalPowerCount = powerCount;
            source.sendSuccess(() -> Component.translatable("commands.apoli.list.pass", target.getName(), finalPowerCount, ComponentUtils.formatList(powers, Component.literal(", "))), true);
        }

        return powerCount;

    }

    private static int hasPower(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ResourceKey<ConfiguredPower<?, ?>> power = PowerTypeArgumentType.getConfiguredPower(context, "power");

        List<LivingEntity> processedLivingTargets = new LinkedList<>();
        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget) || !hasPower(livingTarget, power)) continue;

            processedLivingTargets.add(livingTarget);

        }

        if (!processedLivingTargets.isEmpty()) {
            if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), true);
            else source.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass_count", processedLivingTargets.size()), true);
        }

        else {
            if (targets.size() == 1) source.sendFailure(Component.translatable("commands.execute.conditional.fail"));
            else source.sendFailure(Component.translatable("commands.execute.conditional.fail_count", targets.size()));
        }

        return processedLivingTargets.size();

    }

    private static boolean hasPower(LivingEntity entity, ResourceKey<ConfiguredPower<?, ?>> power) {
        return IPowerContainer.get(entity).map(x -> x.hasPower(power)).orElse(false);
    }

    private static boolean hasPower(LivingEntity entity, ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation powerSource) {
        return IPowerContainer.get(entity).map(x -> x.hasPower(power, powerSource)).orElse(false);
    }

    private static int getSourcesFromPower(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");
        ResourceKey<ConfiguredPower<?, ?>> power = PowerTypeArgumentType.getConfiguredPower(context, "power");
        Component powerName = getPowerName(power, source);
        StringBuilder powerSources = new StringBuilder();

        int powerSourceCount = 0;

        if (!(target instanceof LivingEntity livingTarget)) {
            source.sendFailure(Component.translatable("commands.apoli.sources.fail", target.getDisplayName(), powerName));
            return powerSourceCount;
        }

        IPowerContainer component = ApoliAPI.getPowerContainer(livingTarget);
        if (component != null) {
            String separator = "";
            for (ResourceLocation powerSource : component.getSources(power)) {

                powerSources.append(separator).append(powerSource.toString());
                powerSourceCount++;

                separator = ", ";

            }
        }

        if (powerSourceCount == 0) {
            source.sendFailure(Component.translatable("commands.apoli.sources.fail", target.getName(), powerName));
        } else {
            int finalPowers = powerSourceCount;
            source.sendSuccess(() -> Component.translatable("commands.apoli.sources.pass", target.getName(), finalPowers, powerName, powerSources), true);
        }

        return powerSourceCount;
    }

    private static int removePower(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ResourceKey<ConfiguredPower<?, ?>> power = PowerTypeArgumentType.getConfiguredPower(context, "power");

        LinkedList<Entity> nonLivingTargets = new LinkedList<>();
        LinkedList<LivingEntity> livingTargets = new LinkedList<>();
        LinkedList<LivingEntity> processedLivingTargets = new LinkedList<>();

        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget)) {
                nonLivingTargets.add(target);
                continue;
            }

            livingTargets.add(livingTarget);

            IPowerContainer component = ApoliAPI.getPowerContainer(livingTarget);
            if (component == null) {
                continue;
            }

            if (!revokePowerAllSources(livingTarget, power)) {
                continue;
            }

            processedLivingTargets.add(livingTarget);

        }

        Component powerName = getPowerName(power, source);
        if (!processedLivingTargets.isEmpty()) {
            if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.remove.success.single", processedLivingTargets.getFirst().getDisplayName(), powerName), true);
            else source.sendSuccess(() -> Component.translatable("commands.apoli.remove.success.multiple", processedLivingTargets.size(), powerName), true);
        }

        else if (!livingTargets.isEmpty()) {
            if (livingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.remove.fail.single", livingTargets.getFirst().getDisplayName(), powerName));
            else source.sendFailure(Component.translatable("commands.apoli.remove.fail.multiple", powerName));
        }

        else if (!nonLivingTargets.isEmpty()) {
            if (nonLivingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.remove.invalid_entity", nonLivingTargets.getFirst().getDisplayName()));
            else source.sendFailure(Component.translatable("commands.apoli.remove.invalid_entities", nonLivingTargets.size()));
        }

        return processedLivingTargets.size();

    }

    private static boolean revokePowerAllSources(LivingEntity entity, ResourceKey<ConfiguredPower<?, ?>> power) {
        return IPowerContainer.get(entity).map(component -> {
            List<ResourceLocation> sources = component.getSources(power);
            if (sources.isEmpty()) {
                return false;
            }
            for (ResourceLocation source : sources) {
                component.removePower(power, source);
            }
            if (sources.size() > 0) {
                component.sync();
            }
            return true;
        }).orElse(false);
    }

    private static int clearAllPowers(CommandContext<CommandSourceStack> context, boolean onlyTargetSelf) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        LinkedList<Entity> nonLivingTargets = new LinkedList<>();
        LinkedList<LivingEntity> livingTargets = new LinkedList<>();
        LinkedList<LivingEntity> processedLivingTargets = new LinkedList<>();

        int clearedPowers = 0;
        for (Entity target : targets) {

            if (!(target instanceof LivingEntity livingTarget)) {
                nonLivingTargets.add(target);
                continue;
            }

            livingTargets.add(livingTarget);

            int cleared = clearAllPowers(livingTarget);

            if (cleared == 0) {
                continue;
            }

            clearedPowers += cleared;
            processedLivingTargets.add(livingTarget);

        }

        if (!processedLivingTargets.isEmpty()) {
            final int currentClearedPowers = clearedPowers;
            if (processedLivingTargets.size() == 1) source.sendSuccess(() -> Component.translatable("commands.apoli.clear.success.single", processedLivingTargets.getFirst().getDisplayName(), currentClearedPowers), true);
            else source.sendSuccess(() -> Component.translatable("commands.apoli.clear.success.multiple", processedLivingTargets.size(), currentClearedPowers), true);
        }

        else if (!livingTargets.isEmpty()) {
            if (livingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.clear.fail.single", livingTargets.getFirst().getDisplayName()));
            else source.sendFailure(Component.translatable("commands.apoli.clear.fail.multiple"));
        }

        else if (!nonLivingTargets.isEmpty()) {
            if (nonLivingTargets.size() == 1) source.sendFailure(Component.translatable("commands.apoli.clear.invalid_entity", nonLivingTargets.getFirst().getDisplayName()));
            else source.sendFailure(Component.translatable("commands.apoli.clear.invalid_entities", nonLivingTargets.size()));
        }

        return clearedPowers;

    }

    private static int clearAllPowers(LivingEntity entity) {
        return IPowerContainer.get(entity).map(component -> {
            @NotNull Set<ResourceKey<ConfiguredPower<?, ?>>> powers = component.getPowerTypes(false);
            for (ResourceKey<ConfiguredPower<?, ?>> power : powers) {
                revokePowerAllSources(entity, power);
            }
            if (powers.size() > 0)
                component.sync();
            return powers.size();
        }).orElse(0);
    }

    public static Component getPowerName(ResourceKey<ConfiguredPower<?, ?>> power, CommandSourceStack source) {
        return ApoliAPI.getPowers(source.getServer()).get(power).getData().getName();
    }

    public static int dumpPower(CommandContext<CommandSourceStack> context, boolean indentSpecified) {
        ResourceKey<ConfiguredPower<?, ?>> arg = PowerTypeArgumentType.getConfiguredPower(context, "power");
        ConfiguredPower<?, ?> power = ApoliAPI.getPowers(context.getSource().getServer()).getHolderOrThrow(arg).value();
        String indent = Strings.repeat(' ', indentSpecified ? IntegerArgumentType.getInteger(context, "indent") : 4);
        Component s = ConfiguredPower.CODEC.encodeStart(JsonOps.INSTANCE, power)
                .map(jsonElement -> new JsonTextFormatter(indent).apply(jsonElement)).result().orElseThrow(() -> new CommandRuntimeException(Component.literal("Failed to encode " + arg)));
        context.getSource().sendSuccess(() -> s, false);
        return 1;
    }

    public static int checkCondition(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, "target");
        ConfiguredEntityCondition<?, ?> condition = EntityConditionArgument.getEntityCondition(context, "condition_json");
        if (condition.check(target)) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
            return 1;
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("commands.execute.conditional.fail"), false);
            return 0;
        }
    }

}
