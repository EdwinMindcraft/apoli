package io.github.edwinmindcraft.apoli.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class PowerUtils {
	private static final Random RANDOM = new Random();

	public static AttributeModifier staticModifier(ResourceLocation id, double value, AttributeModifier.Operation operation, Object... fixed) {
		return new AttributeModifier(id, value, operation);
	}
}
