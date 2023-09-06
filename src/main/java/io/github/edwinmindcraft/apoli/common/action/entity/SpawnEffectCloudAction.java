package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.SpawnEffectCloudConfiguration;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnEffectCloudAction extends EntityAction<SpawnEffectCloudConfiguration> {

	public SpawnEffectCloudAction() {
		super(SpawnEffectCloudConfiguration.CODEC);
	}

	@Override
	public void execute(SpawnEffectCloudConfiguration configuration, Entity entity) {
		AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
		if (entity instanceof LivingEntity)
			areaEffectCloudEntity.setOwner((LivingEntity) entity);
		areaEffectCloudEntity.setRadius(configuration.radius());
		areaEffectCloudEntity.setRadiusOnUse(configuration.radiusOnUse());
		areaEffectCloudEntity.setWaitTime(configuration.waitTime());
		areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
		List<MobEffectInstance> effects = configuration.effects().getContent().stream().map(MobEffectInstance::new).collect(Collectors.toList());
		areaEffectCloudEntity.setFixedColor(PotionUtils.getColor(effects));
		effects.forEach(areaEffectCloudEntity::addEffect);
		entity.level().addFreshEntity(areaEffectCloudEntity);
	}
}
