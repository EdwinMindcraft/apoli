package io.github.apace100.apoli.command;

import com.google.gson.JsonObject;
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
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PowerSourceArgumentType implements ArgumentType<ResourceLocation> {
	public static PowerSourceArgumentType powerSource(String target) {
		return new PowerSourceArgumentType(target);
	}

	public static ConfiguredPower<?, ?> getConfiguredPower(CommandContext<CommandSourceStack> context, String argumentName) {
		ResourceLocation argument = context.getArgument(argumentName, ResourceLocation.class);
		return CalioAPI.getDynamicRegistries(context.getSource().getServer()).get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY)
				.getOptional(argument).orElseThrow(() -> new CommandRuntimeException(new TranslatableComponent("arguments.apoli.power_source.fail", argument)));
	}

	private final String target;

	public PowerSourceArgumentType(String target) {

		this.target = target;
	}

	public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
		return ResourceLocation.read(reader);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack) {
			try {
				Set<ResourceLocation> collect = EntityArgument.getEntities((CommandContext<CommandSourceStack>) context, this.target).stream().map(ApoliAPI::getPowerContainer).filter(Objects::nonNull).flatMap(x -> x.getPowerNames().stream().flatMap(name -> x.getSources(name).stream())).collect(Collectors.toSet());
				return SharedSuggestionProvider.suggestResource(collect, builder);
			} catch (CommandSyntaxException e) {
				return Suggestions.empty();
			}
		}
		return Suggestions.empty();
	}

	public static class Serializer implements ArgumentSerializer<PowerSourceArgumentType> {
		@Override
		public void serializeToNetwork(PowerSourceArgumentType argument, FriendlyByteBuf buf) {
			buf.writeUtf(argument.target);
		}

		@Override
		public PowerSourceArgumentType deserializeFromNetwork(FriendlyByteBuf buf) {
			return PowerSourceArgumentType.powerSource(buf.readUtf());
		}

		@Override
		public void serializeToJson(PowerSourceArgumentType argument, JsonObject json) {
			json.addProperty("target", argument.target);
		}
	}
}
