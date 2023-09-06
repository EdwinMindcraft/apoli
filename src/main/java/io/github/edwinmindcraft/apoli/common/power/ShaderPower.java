package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.ApoliClient;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ShaderConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ShaderPower extends PowerFactory<ShaderConfiguration> {

	public ShaderPower() {
		super(ShaderConfiguration.CODEC);
	}

	@Override
	public void tick(ConfiguredPower<ShaderConfiguration, ?> configuration, Entity entity) {
		MutableBoolean powerData = configuration.getPowerData(entity, MutableBoolean::new);
		boolean active = this.isActive(configuration, entity);
		if (powerData.booleanValue() != active) {
			powerData.setValue(active);
			if (entity.level().isClientSide())
				this.statusChanged();
		}
	}

	@Override
	public void onAdded(ConfiguredPower<ShaderConfiguration, ?> configuration, Entity entity) {
		this.statusChanged();
	}

	@Override
	protected void onRemoved(ShaderConfiguration configuration, Entity entity) {
		this.statusChanged();
	}

	private void statusChanged() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ApoliClient.shouldReapplyShaders = true);
	}
}
