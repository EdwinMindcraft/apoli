package io.github.edwinmindcraft.apoli.common.condition.damage;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import net.minecraft.world.damagesource.DamageSource;

public class NameCondition extends DamageCondition<FieldConfiguration<String>> {

	public NameCondition() {
		super(FieldConfiguration.codec(Codec.STRING, "name"));
	}

	// TODO: Fix https://github.com/apace100/origins-fabric/issues/735 as soon as upstream does.
	@Override
	public boolean check(FieldConfiguration<String> configuration, DamageSource source, float amount) {
		return configuration.value().equals(source.msgId);
	}
}
