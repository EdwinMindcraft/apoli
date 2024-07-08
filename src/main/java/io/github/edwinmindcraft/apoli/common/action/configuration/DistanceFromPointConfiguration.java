package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.DoubleComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Function;

/**
 * This is a conversion of Alluysl's work on distance object.
 * While the code is significantly different, it should do exactly the same thing.
 *
 * @param reference                 The reference point
 * @param offset                    The offset vector from the reference point
 * @param ignoreX                   Whether to ignore the x-axis for distance calculations
 * @param ignoreY                   Whether to ignore the y-axis for distance calculations
 * @param ignoreZ                   Whether to ignore the z-axis for distance calculations
 * @param shape                     The distance type (norms 1, 2 or infinity)
 * @param scaleReferenceToDimension Whether to scale the reference's coordinates according to the dimension it's in and the player is in
 * @param scaleDistanceToDimension  Whether to scale the calculated distance to the current dimension
 * @param comparison                The distance comparison
 * @param resultOnWrongDimension    If set and the dimension is not the same as the reference's, the value to set the condition to
 * @param roundToDigit              If set, rounds the distance to this amount of digits (e.g. 0 for unitary values, 1 for decimals, -1 for multiples of ten)
 *
 * @author Alluysl
 * @author Edwin
 */
public record DistanceFromPointConfiguration(ReferencePoint reference, Vec3 offset, boolean ignoreX, boolean ignoreY,
											 boolean ignoreZ, Shape shape, boolean scaleReferenceToDimension,
											 boolean scaleDistanceToDimension, DoubleComparisonConfiguration comparison,
											 Optional<Boolean> resultOnWrongDimension,
											 Optional<Integer> roundToDigit) implements IDynamicFeatureConfiguration {
	public static final MapCodec<Vec3> OFFSET_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SerializableDataTypes.VECTOR.optionalFieldOf("offset", Vec3.ZERO).forGetter(Function.identity()),
			SerializableDataTypes.VECTOR.optionalFieldOf("coordinates", Vec3.ZERO).forGetter(x -> Vec3.ZERO)
	).apply(instance, Vec3::add));

	public static MapCodec<DistanceFromPointConfiguration> codec(ReferencePoint reference) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				SerializableDataType.enumValue(ReferencePoint.class).optionalFieldOf("reference", reference).forGetter(DistanceFromPointConfiguration::reference),
				OFFSET_CODEC.forGetter(DistanceFromPointConfiguration::offset),
				CalioCodecHelper.BOOL.optionalFieldOf("ignore_x", false).forGetter(DistanceFromPointConfiguration::ignoreX),
				CalioCodecHelper.BOOL.optionalFieldOf("ignore_y", false).forGetter(DistanceFromPointConfiguration::ignoreY),
				CalioCodecHelper.BOOL.optionalFieldOf("ignore_z", false).forGetter(DistanceFromPointConfiguration::ignoreZ),
				SerializableDataType.enumValue(Shape.class).optionalFieldOf("shape", Shape.CUBE).forGetter(DistanceFromPointConfiguration::shape),
				CalioCodecHelper.BOOL.optionalFieldOf("scale_reference_to_dimension", true).forGetter(DistanceFromPointConfiguration::scaleReferenceToDimension),
				CalioCodecHelper.BOOL.optionalFieldOf("scale_distance_to_dimension", false).forGetter(DistanceFromPointConfiguration::scaleDistanceToDimension),
				DoubleComparisonConfiguration.MAP_CODEC.forGetter(DistanceFromPointConfiguration::comparison),
				CalioCodecHelper.BOOL.optionalFieldOf("result_on_wrong_dimension").forGetter(DistanceFromPointConfiguration::resultOnWrongDimension),
				CalioCodecHelper.INT.optionalFieldOf("round_to_digit").forGetter(DistanceFromPointConfiguration::roundToDigit)
		).apply(instance, DistanceFromPointConfiguration::new));
	}

	/**
	 * Tests the distance from a given point to the point described by this object.
	 *
	 * @param entity   The entity whose spawn would be used if the code were implemented
	 * @param position The position to get the distance from
	 * @param level    The level used for calculations
	 *
	 * @return The result of the distance comparison
	 */
	public boolean test(@Nullable Entity entity, Vec3 position, Level level) {
		double scale = level.dimensionType().coordinateScale();
		Vec3 point = this.reference().getPoint(entity, level, this.resultOnWrongDimension().isPresent());
		if (point == null)
			return this.resultOnWrongDimension().get();
		point = point.add(this.offset());
		if (this.scaleReferenceToDimension() && (point.x() != 0 || point.z() != 0)) {
			if (scale == 0)
				return this.comparison().check(Double.POSITIVE_INFINITY);
			point = point.multiply(1 / scale, 1, 1 / scale);
		}
		Vec3 delta = point.subtract(position);
		delta = new Vec3(this.ignoreX() ? 0 : Math.abs(delta.x()), this.ignoreY() ? 0 : Math.abs(delta.y()), this.ignoreZ() ? 0 : Math.abs(delta.z()));
		if (this.scaleDistanceToDimension())
			delta.multiply(scale, 1, scale);
		double distance = Shape.getDistance(this.shape(), delta.x(), delta.y(), delta.z());
		if (this.roundToDigit().isPresent())
			distance = new BigDecimal(distance).setScale(this.roundToDigit().get(), RoundingMode.HALF_UP).doubleValue();
		return this.comparison().check(distance);
	}
}
