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
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class PowerTypeArgumentType implements ArgumentType<ResourceLocation> {

	public static PowerTypeArgumentType power() {
		return new PowerTypeArgumentType();
	}

	public static ResourceKey<ConfiguredPower<?, ?>> getConfiguredPower(CommandContext<CommandSourceStack> context, String argumentName) {
		ResourceLocation argument = context.getArgument(argumentName, ResourceLocation.class);
		if (!ApoliAPI.getPowers(context.getSource().getServer()).containsKey(argument))
			throw new CommandRuntimeException(Component.translatable("commands.apoli.power_not_found", argument));
		return ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, argument);
	}

	public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
		return ResourceLocation.read(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ApoliAPI.getPowers().keySet(), builder);
	}
}
