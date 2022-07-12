package io.github.apace100.apoli.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PowerSourceArgumentType implements ArgumentType<ResourceLocation> {
	public static PowerSourceArgumentType powerSource(String target) {
		return new PowerSourceArgumentType(target);
	}

	public static PowerSourceArgumentType powerSource(CommandBuildContext context) {
		return new PowerSourceArgumentType(null);
	}

	public static ConfiguredPower<?, ?> getConfiguredPower(CommandContext<CommandSourceStack> context, String argumentName) {
		ResourceLocation argument = context.getArgument(argumentName, ResourceLocation.class);
		return CalioAPI.getDynamicRegistries(context.getSource().getServer()).get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY)
				.getOptional(argument).orElseThrow(() -> new CommandRuntimeException(Component.translatable("arguments.apoli.power_source.fail", argument)));
	}

	private final String target;

	public PowerSourceArgumentType(@Nullable String target) {
		this.target = target;
	}

	public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
		return ResourceLocation.read(reader);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack && this.target != null) {
			try {
				Set<ResourceLocation> collect = EntityArgument.getEntities((CommandContext<CommandSourceStack>) context, this.target).stream().map(ApoliAPI::getPowerContainer).filter(Objects::nonNull).flatMap(x -> x.getPowerTypes(true).stream().flatMap(name -> x.getSources(name).stream())).collect(Collectors.toSet());
				return SharedSuggestionProvider.suggestResource(collect, builder);
			} catch (CommandSyntaxException e) {
				return Suggestions.empty();
			}
		}
		return Suggestions.empty();
	}
}
