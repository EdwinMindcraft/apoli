package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public final class ExplodeConfiguration implements IDynamicFeatureConfiguration {
	public static final Codec<ExplodeConfiguration> CODEC = RecordCodecBuilder.create(instance -> /*, t6*/ instance.group(
			CalioCodecHelper.FLOAT.fieldOf("power").forGetter(ExplodeConfiguration::power),
			CalioCodecHelper.optionalField(SerializableDataTypes.DESTRUCTION_TYPE, "destruction_type", Explosion.BlockInteraction.BREAK).forGetter(ExplodeConfiguration::destructionType),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "damage_self", true).forGetter(ExplodeConfiguration::damageSelf),
			ConfiguredBlockCondition.optional("indestructible", Apoli.identifier("deny")).forGetter(ExplodeConfiguration::indestructible),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "create_fire", false).forGetter(ExplodeConfiguration::createFire)
			//ConfiguredBlockCondition.optional("destructible").forGetter(x -> Optional.empty()) //Ignored
	).apply(instance, ExplodeConfiguration::new));
	private final float power;
	private final Explosion.BlockInteraction destructionType;
	private final boolean damageSelf;
	private final Holder<ConfiguredBlockCondition<?, ?>> indestructible;
	private final boolean createFire;

	private final transient Lazy<ExplosionDamageCalculator> explosionCalculator;

	public ExplodeConfiguration(float power, Explosion.BlockInteraction destructionType, boolean damageSelf, Holder<ConfiguredBlockCondition<?, ?>> indestructible, boolean createFire) {
		this.power = power;
		this.destructionType = destructionType;
		this.damageSelf = damageSelf;
		this.indestructible = indestructible;
		this.createFire = createFire;
		this.explosionCalculator = Lazy.of(() -> !this.indestructible().isBound() ? new ExplosionDamageCalculator() : new ExplosionDamageCalculator() {
			@Override
			@NotNull
			public Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluid) {
				Optional<Float> def = super.getBlockExplosionResistance(explosion, world, pos, state, fluid);
				Optional<Float> ovr = ConfiguredBlockCondition.check(ExplodeConfiguration.this.indestructible(), (LevelReader) world, pos, () -> state) ? Optional.of(100F) : Optional.empty();
				return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
			}
		});
	}

	public float power() {return this.power;}

	public Explosion.BlockInteraction destructionType() {return this.destructionType;}

	public boolean damageSelf() {return this.damageSelf;}

	public Holder<ConfiguredBlockCondition<?, ?>> indestructible() {return this.indestructible;}

	public boolean createFire() {return this.createFire;}

	@NotNull
	public ExplosionDamageCalculator calculator() {
		return this.explosionCalculator.get();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ExplodeConfiguration) obj;
		return Float.floatToIntBits(this.power) == Float.floatToIntBits(that.power) &&
			   Objects.equals(this.destructionType, that.destructionType) &&
			   this.damageSelf == that.damageSelf &&
			   Objects.equals(this.indestructible, that.indestructible) &&
			   this.createFire == that.createFire;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.power, this.destructionType, this.damageSelf, this.indestructible, this.createFire);
	}

	@Override
	public String toString() {
		return "ExplodeConfiguration[" +
			   "power=" + this.power + ", " +
			   "destructionType=" + this.destructionType + ", " +
			   "damageSelf=" + this.damageSelf + ", " +
			   "indestructible=" + this.indestructible + ", " +
			   "createFire=" + this.createFire + ']';
	}
}
