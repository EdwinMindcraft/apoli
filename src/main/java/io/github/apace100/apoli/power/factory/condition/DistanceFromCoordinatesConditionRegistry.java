package io.github.apace100.apoli.power.factory.condition;

import com.mojang.serialization.MapCodec;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.Comparison;
import io.github.edwinmindcraft.apoli.common.action.configuration.DistanceFromPointConfiguration;
import io.github.edwinmindcraft.apoli.common.action.configuration.ReferencePoint;
import io.github.edwinmindcraft.apoli.common.action.entity.DistanceFromPointEntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.block.DistanceFromPointBlockCondition;
import io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters;

import java.util.ArrayList;

/**
 * @author Alluysl
 * Handles the registry of the distance_from_spawn condition in both block and entity conditions to avoid duplicating code.
 */
public class DistanceFromCoordinatesConditionRegistry {

	private static final ArrayList<Object> previousWarnings = new ArrayList<>();

	private static void warnOnce(String warning, Object key) {
		if (!previousWarnings.contains(key)) {
			previousWarnings.add(key);
			Apoli.LOGGER.warn(warning);
		}
	}

	public static void warnOnce(String warning) {warnOnce(warning, warning);}

	/**
	 * Warns the user of an issue getting an information needed for expected behavior, but only once (doesn't spam the console).
	 *
	 * @param object     the object that couldn't be acquired
	 * @param from       the object that was supposed to provide the required object
	 * @param assumption the result assumed because of the lack of information
	 *
	 * @return the assumed result
	 */
	public static <T> T warnCouldNotGetObject(String object, String from, T assumption) {
		warnOnce("Could not retrieve " + object + " from " + from + " for distance_from_spawn condition, assuming " + assumption + " for condition.");
		return assumption;
	}

	/**
	 * Returns an array of aliases for the condition.
	 */
	private static String[] getAliases() {
		return new String[]{"distance_from_spawn", "distance_from_coordinates"};
	}

	private static MapCodec<DistanceFromPointConfiguration> getSerializableData(String alias) {
		// Using doubles and not ints because the player position is a vector of doubles and the sqrt function (for the distance) returns a double so we might as well use that precision
		return DistanceFromPointConfiguration.codec(alias.equals("distance_from_coordinates") ? ReferencePoint.WORLD_ORIGIN : ReferencePoint.WORLD_SPAWN);
        /*return new SerializableData()
            .add("reference", SerializableDataTypes.STRING, alias.equals("distance_from_coordinates") ? "world_origin" : "world_spawn")  // the reference point
//          .add("check_modified_spawn", SerializableDataTypes.BOOLEAN, true) // whether to check for modified spawns
            .add("offset", SerializableDataTypes.VECTOR, Vec3.ZERO) // offset to the reference point
            .add("coordinates", SerializableDataTypes.VECTOR, Vec3.ZERO) // adds up (instead of replacing, for simplicity) to the prior for aliasing
            .add("ignore_x", SerializableDataTypes.BOOLEAN, false) // ignore the axis in the distance calculation
            .add("ignore_y", SerializableDataTypes.BOOLEAN, false) // idem
            .add("ignore_z", SerializableDataTypes.BOOLEAN, false) // idem
            .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE) // the shape / distance type
            .add("scale_reference_to_dimension", SerializableDataTypes.BOOLEAN, true) // whether to scale the reference's coordinates according to the dimension it's in and the player is in
            .add("scale_distance_to_dimension", SerializableDataTypes.BOOLEAN, false) // whether to scale the calculated distance to the current dimension
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.DOUBLE)
            .add("result_on_wrong_dimension", SerializableDataTypes.BOOLEAN, null) // if set and the dimension is not the same as the reference's, the value to set the condition to
            .add("round_to_digit", SerializableDataTypes.INT, null); // if set, rounds the distance to this amount of digits (e.g. 0 for unitary values, 1 for decimals, -1 for multiples of ten)*/
	}

	/**
	 * Infers the logically meaningful result of a distance comparison for out of bounds points (different dimension with corresponding parameter set, or infinite coordinates).
	 *
	 * @param comparison the comparison set in the data
	 *
	 * @return the result of that comparison against out-of-bounds points
	 */
	private static boolean compareOutOfBounds(Comparison comparison) {
		return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;
	}

	// Watch Java generic type erasure destroy DRY

	public static void registerBlockConditions() {
		for (String alias : getAliases())
			ApoliRegisters.BLOCK_CONDITIONS.register(alias, () -> new DistanceFromPointBlockCondition(getSerializableData(alias)));
	}

	public static void registerEntityCondition() {
		for (String alias : getAliases())
			ApoliRegisters.ENTITY_CONDITIONS.register(alias, () -> new DistanceFromPointEntityCondition(getSerializableData(alias)));
	}
}
