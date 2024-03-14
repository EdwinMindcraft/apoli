package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public record ColorConfiguration(float red, float green, float blue,
								 float alpha) implements IDynamicFeatureConfiguration {
	public static ColorConfiguration DEFAULT = new ColorConfiguration(1.0F, 1.0F, 1.0F, 1.0F);

	public static final Codec<ColorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "red", 1.0F).forGetter(ColorConfiguration::red),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "green", 1.0F).forGetter(ColorConfiguration::green),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "blue", 1.0F).forGetter(ColorConfiguration::blue),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "alpha", 1.0F).forGetter(ColorConfiguration::alpha)
	).apply(instance, ColorConfiguration::new));

	public static final MapCodec<ColorConfiguration> NO_ALPHA = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "red", 1.0F).forGetter(ColorConfiguration::red),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "green", 1.0F).forGetter(ColorConfiguration::green),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "blue", 1.0F).forGetter(ColorConfiguration::blue)
	).apply(instance, ColorConfiguration::new));

	public static Optional<ColorConfiguration> forPower(Entity entity, PowerFactory<ColorConfiguration> factory) {
		return PowerContainer.getPowers(entity, factory).stream().map(Holder::value).map(ConfiguredPower::getConfiguration).reduce(ColorConfiguration::merge);
	}

	public ColorConfiguration(float red, float green, float blue) {
		this(red, green, blue, 1.0F);
	}

	public ColorConfiguration merge(ColorConfiguration other) {
		return new ColorConfiguration(this.red() * other.red(), this.green() * other.green(), this.blue() * other.blue(), Math.min(this.alpha(), other.alpha()));
	}

	public ColorConfiguration multiply(float value) {
		return new ColorConfiguration(this.red() * value, this.green() * value, this.blue() * value, this.alpha() * value);
	}

	public ColorConfiguration withAlpha(float alpha) {
		return new ColorConfiguration(this.red(), this.green(), this.blue(), alpha);
	}

	public int asRGB() {
		int blue = Mth.clamp((int) (this.blue() * 255), 0, 255);
		int green = Mth.clamp((int) (this.green() * 255), 0, 255);
		int red = Mth.clamp((int) (this.red() * 255), 0, 255);
		return (((red << 8) | green) << 8) | blue;
	}
}
