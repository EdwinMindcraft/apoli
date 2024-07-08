package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IAttributeModifyingPowerConfiguration;
import io.github.edwinmindcraft.apoli.common.util.PowerUtils;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.function.Supplier;

public final class LavaVisionConfiguration implements IAttributeModifyingPowerConfiguration {
	public static final MapCodec<LavaVisionConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.FLOAT.fieldOf("s").forGetter(LavaVisionConfiguration::s),
			CalioCodecHelper.FLOAT.fieldOf("v").forGetter(LavaVisionConfiguration::v)
	).apply(instance, LavaVisionConfiguration::new));
	private final float s;
	private final float v;

	private Supplier<ListConfiguration<AttributeModifier>> modifiers;

	public LavaVisionConfiguration(float s, float v) {
		this.s = s;
		this.v = v;
	}

	public void createModifiers(ResourceLocation powerId) {
		this.modifiers = Suppliers.memoize(() -> ListConfiguration.of(PowerUtils.staticModifier(powerId, this.v() - 1.0, AttributeModifier.Operation.ADD_VALUE)));
	}

	@Override
	public ListConfiguration<AttributeModifier> modifiers() {
		return this.modifiers.get();
	}

	public float s() {
		return this.s;
	}

	public float v() {
		return this.v;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (LavaVisionConfiguration) obj;
		return Float.floatToIntBits(this.s) == Float.floatToIntBits(that.s) &&
			   Float.floatToIntBits(this.v) == Float.floatToIntBits(that.v);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.s, this.v);
	}

	@Override
	public String toString() {
		return "LavaVisionConfiguration[" +
			   "s=" + this.s + ", " +
			   "v=" + this.v + ']';
	}
}
