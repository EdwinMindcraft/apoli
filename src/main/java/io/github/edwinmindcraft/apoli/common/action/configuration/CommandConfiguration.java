package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalInt;

public record CommandConfiguration(String command) implements IDynamicFeatureConfiguration {
	public static final MapCodec<CommandConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("command").forGetter(CommandConfiguration::command)
	).apply(instance, CommandConfiguration::new));

	public static final Codec<CommandConfiguration> CODEC = MAP_CODEC.codec();

	private static CommandSource getSource(Entity entity) {
		boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
		return ApoliConfigs.SERVER.executeCommand.showOutput.get() && validOutput ? entity : CommandSource.NULL;
	}

	public static OptionalInt executeAt(Entity entity, Vec3 position, String command) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			CommandSourceStack source = new CommandSourceStack(
					getSource(entity),
					position,
					entity.getRotationVector(),
					entity.level() instanceof ServerLevel sl ? sl : null,
					ApoliConfigs.SERVER.executeCommand.permissionLevel.get(),
					entity.getName().getString(),
					entity.getDisplayName(),
					server,
					entity);
			return OptionalInt.of(server.getCommands().performPrefixedCommand(source, command));
		}
		return OptionalInt.empty();
	}

	public OptionalInt execute(Entity entity) {
		return executeAt(entity, entity.position(), this.command());
	}

	public OptionalInt execute(Level world, BlockPos pos) {
		MinecraftServer server = world.getServer();
		if (server != null) {
			String blockName = world.getBlockState(pos).getBlock().getDescriptionId();
			CommandSourceStack source = new CommandSourceStack(
					ApoliConfigs.SERVER.executeCommand.showOutput.get() ? server : CommandSource.NULL,
					new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
					new Vec2(0, 0),
					(ServerLevel) world,
					ApoliConfigs.SERVER.executeCommand.permissionLevel.get(),
					blockName,
					Component.translatable(blockName),
					server,
					null);
			return OptionalInt.of(server.getCommands().performPrefixedCommand(source, this.command()));
		}
		return OptionalInt.empty();
	}
}
