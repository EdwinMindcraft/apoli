package io.github.apace100.apoli.util;

import net.minecraftforge.common.ForgeConfigSpec;

public class ApoliConfigServer {
	public final ExecuteCommand executeCommand;
	public final ForgeConfigSpec.BooleanValue enforceFoodRestrictions;
    public final ForgeConfigSpec.BooleanValue separateSpawnFindingThread;

	public ApoliConfigServer(ForgeConfigSpec.Builder builder) {
		builder.push("execute_command");
		this.executeCommand = new ExecuteCommand(builder);
		builder.pop();
		builder.push("prevent_usage");
		this.enforceFoodRestrictions = builder.comment("Whether to enforce food restrictions or not").translation("config.apoli.prevent_usage.enforce_food_restrictions").define("enforce_food_restrictions", true);
		builder.pop();
        builder.push("modify_player_spawn");
        this.separateSpawnFindingThread = builder.comment("Whether finding a spawn for Modify Player Spawn should happen on a separate thread or not").translation("config.apoli.modify_player_spawn.separate_finding_thread").define("separate_finding_thread", true);
	    builder.pop();
    }

	/**
	 * Putting this on the server allows for world specific configurations.
	 */
	public static class ExecuteCommand {
		public final ForgeConfigSpec.IntValue permissionLevel;
		public final ForgeConfigSpec.BooleanValue showOutput;

		public ExecuteCommand(ForgeConfigSpec.Builder builder) {
			this.permissionLevel = builder
					.translation("text.autoconfig.power_config.option.executeCommand.permissionLevel")
					.comment("Permission level used. 2 is enough for everything except server and debug commands.")
					.defineInRange("permission_level", 2, 0, 4);
			this.showOutput = builder
					.translation("text.autoconfig.power_config.option.executeCommand.showOutput")
					.comment("If true, this sends the command results in chat. Shouldn't be used during actual gameplay")
					.define("show_output", false);
		}
	}
}
