package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FireProjectileConfiguration(int cooldown, HudRender hudRender, EntityType<?> entityType,
										  int projectileCount, float speed, float divergence,
										  @Nullable SoundEvent soundEvent, @Nullable CompoundTag tag,
										  IActivePower.Key key, int interval,
										  int startDelay) implements IActiveCooldownPowerConfiguration {

	public static final Codec<FireProjectileConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(FireProjectileConfiguration::cooldown),
			ApoliDataTypes.HUD_RENDER.fieldOf("hud_render").forGetter(FireProjectileConfiguration::hudRender),
			Registry.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(FireProjectileConfiguration::entityType),
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "count", 1).forGetter(FireProjectileConfiguration::projectileCount),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "speed", 1.5F).forGetter(FireProjectileConfiguration::speed),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "divergence", 1.0F).forGetter(FireProjectileConfiguration::divergence),
			CalioCodecHelper.optionalField(SerializableDataTypes.SOUND_EVENT, "sound").forGetter(x -> Optional.ofNullable(x.soundEvent())),
			CalioCodecHelper.optionalField(SerializableDataTypes.NBT, "tag").forGetter(x -> Optional.ofNullable(x.tag())),
			CalioCodecHelper.optionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(FireProjectileConfiguration::key),
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "interval", 0).forGetter(FireProjectileConfiguration::interval),
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "start_delay", 0).forGetter(FireProjectileConfiguration::startDelay)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) -> new FireProjectileConfiguration(t1, t2, t3, t4, t5, t6, t7.orElse(null), t8.orElse(null), t9, t10, t11)));

	public void playSound(Entity player) {
		if (this.soundEvent != null) {
			player.level.playSound(null, player.getX(), player.getY(), player.getZ(), this.soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level.getRandom().nextFloat() * 0.4F + 0.8F));
		}
	}

	public void fireProjectile(Entity source) {
		Entity entity = this.entityType().create(source.level);
		if (entity == null) {
			return;
		}
		Vec3 rotationVector = source.getLookAngle();
		float yaw = source.getYRot();
		float pitch = source.getXRot();
		Vec3 spawnPos = source.position().add(0, source.getEyeHeightAccess(source.getPose(), source.getDimensions(source.getPose())), 0).add(rotationVector);
		entity.moveTo(spawnPos.x(), spawnPos.y(), spawnPos.z(), pitch, yaw);
		if (entity instanceof Projectile projectile) {
			if (entity instanceof AbstractHurtingProjectile ahp) {
				ahp.xPower = rotationVector.x * this.speed;
				ahp.yPower = rotationVector.y * this.speed;
				ahp.zPower = rotationVector.z * this.speed;
			}
			projectile.setOwner(source);
			projectile.shootFromRotation(source, pitch, yaw, 0F, this.speed(), this.divergence());
		} else {
			float f = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
			float g = -Mth.sin(pitch * 0.017453292F);
			float h = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
			Vec3 vec3d = (new Vec3(f, g, h)).normalize().add(source.level.getRandom().nextGaussian() * 0.007499999832361937D * (double) this.divergence(), source.level.getRandom().nextGaussian() * 0.007499999832361937D * (double) this.divergence(), source.level.getRandom().nextGaussian() * 0.007499999832361937D * (double) this.divergence()).scale(this.speed());
			entity.setDeltaMovement(vec3d);
			Vec3 entityVelo = source.getDeltaMovement();
			entity.setDeltaMovement(entity.getDeltaMovement().add(entityVelo.x, source.isOnGround() ? 0.0D : entityVelo.y, entityVelo.z));
		}
		if (this.tag != null) {
			CompoundTag mergedTag = entity.saveWithoutId(new CompoundTag());
			mergedTag.merge(this.tag);
			entity.load(mergedTag);
		}
		source.level.addFreshEntity(entity);
	}

	@Override
	public int duration() {
		return this.cooldown();
	}
}
