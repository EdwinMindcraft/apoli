package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;

public class GroundedPower extends PowerFactory<NoConfiguration> {
    public GroundedPower() {
        super(NoConfiguration.CODEC);
    }
}
