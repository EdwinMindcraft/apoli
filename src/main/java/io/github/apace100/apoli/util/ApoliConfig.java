package io.github.apace100.apoli.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ApoliConfig {
	public final Experiments experiments;

	public ApoliConfig(ModConfigSpec.Builder builder) {
		builder.push("experiments");
		this.experiments = new Experiments(builder);
		builder.pop();
	}

	public static class Experiments {
		public final ModConfigSpec.BooleanValue hud;

		public Experiments(ModConfigSpec.Builder builder) {
			this.hud = builder
					.comment("Enabled the hud experiment, adding custom hud renderers")
					.translation("config.apoli.experiment.hud")
					.define("hud", false);
		}
	}
}
