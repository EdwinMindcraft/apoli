package io.github.apace100.apoli.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ApoliConfigs {

	public static final ModConfigSpec COMMON_SPECS;
	public static final ModConfigSpec CLIENT_SPECS;
	public static final ModConfigSpec SERVER_SPECS;

	public static final ApoliConfig COMMON;
	public static final ApoliConfigClient CLIENT;
	public static final ApoliConfigServer SERVER;

	static {
		final Pair<ApoliConfig, ModConfigSpec> common = new ModConfigSpec.Builder().configure(ApoliConfig::new);
		COMMON_SPECS = common.getRight();
		COMMON = common.getLeft();
		final Pair<ApoliConfigClient, ModConfigSpec> client = new ModConfigSpec.Builder().configure(ApoliConfigClient::new);
		CLIENT_SPECS = client.getRight();
		CLIENT = client.getLeft();
		final Pair<ApoliConfigServer, ModConfigSpec> server = new ModConfigSpec.Builder().configure(ApoliConfigServer::new);
		SERVER_SPECS = server.getRight();
		SERVER = server.getLeft();
	}
}
