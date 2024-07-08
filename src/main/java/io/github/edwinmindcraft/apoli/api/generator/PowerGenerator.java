package io.github.edwinmindcraft.apoli.api.generator;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.apace100.calio.data.JsonDataProvider;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class PowerGenerator extends JsonDataProvider<ConfiguredPower<?, ?>> {

	protected PowerGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper, "powers", PackType.SERVER_DATA);
	}

	@Override
	protected JsonElement asJson(ConfiguredPower<?, ?> input) {
		return ConfiguredPower.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, input).getOrThrow();
	}

	@Override
	public @NotNull String getName() {
		return "Power Generator: " + this.modid;
	}
}
