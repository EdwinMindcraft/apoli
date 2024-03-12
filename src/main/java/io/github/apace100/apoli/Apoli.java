package io.github.apace100.apoli;

import io.github.apace100.apoli.util.ApoliConfig;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.apace100.apoli.util.GainedPowerCriterion;
import io.github.apace100.apoli.util.Scheduler;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Apoli.MODID)
public class Apoli {

	public static final Scheduler SCHEDULER = new Scheduler();

	public static final String MODID = "apoli";
	public static final Logger LOGGER = LogManager.getLogger(Apoli.class);
	//public static String VERSION = "";
	//public static int[] SEMVER;

	//public static final AbilitySource LEGACY_POWER_SOURCE = Pal.getAbilitySource(Apoli.identifier("power_source"));

	public static final boolean PERFORM_VERSION_CHECK = false;

	public static ResourceLocation identifier(String path) {
		return new ResourceLocation(MODID, path);
	}

	public static ApoliConfig config;

	public Apoli(IEventBus bus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ApoliConfigs.COMMON_SPECS);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ApoliConfigs.CLIENT_SPECS);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ApoliConfigs.SERVER_SPECS);

		//ModPacketsC2S.register();

		//OrderedResourceListeners.register(new PowerTypes()).complete();

		CriteriaTriggers.register(GainedPowerCriterion.INSTANCE);

		ApoliCommon.initialize(bus);
		if (FMLEnvironment.dist == Dist.CLIENT) ApoliClient.initialize(bus);
		LOGGER.info("Apoli " + ModLoadingContext.get().getActiveContainer().getModInfo().getVersion() + " has initialized. Ready to power up your game!");
	}
}
