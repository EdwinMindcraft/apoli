package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
                                          int startDelay, Holder<ConfiguredEntityAction<?, ?>> projectileAction,
                                          Holder<ConfiguredEntityAction<?, ?>> shooterAction) implements IActiveCooldownPowerConfiguration {

	public static final Codec<FireProjectileConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(FireProjectileConfiguration::cooldown),
			ExtraCodecs.strictOptionalField(ApoliDataTypes.HUD_RENDER, "hud_render", HudRender.DONT_RENDER).forGetter(FireProjectileConfiguration::hudRender),
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(FireProjectileConfiguration::entityType),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "count", 1).forGetter(FireProjectileConfiguration::projectileCount),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "speed", 1.5F).forGetter(FireProjectileConfiguration::speed),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "divergence", 1.0F).forGetter(FireProjectileConfiguration::divergence),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.SOUND_EVENT, "sound").forGetter(x -> Optional.ofNullable(x.soundEvent())),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.NBT, "tag").forGetter(x -> Optional.ofNullable(x.tag())),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(FireProjectileConfiguration::key),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "interval", 0).forGetter(FireProjectileConfiguration::interval),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "start_delay", 0).forGetter(FireProjectileConfiguration::startDelay),
            ConfiguredEntityAction.optional("projectile_action").forGetter(FireProjectileConfiguration::projectileAction),
            ConfiguredEntityAction.optional("shooter_action").forGetter(FireProjectileConfiguration::shooterAction)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) -> new FireProjectileConfiguration(t1, t2, t3, t4, t5, t6, t7.orElse(null), t8.orElse(null), t9, t10, t11, t12, t13)));

	public void playSound(Entity player) {
		if (this.soundEvent != null) {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), this.soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level().getRandom().nextFloat() * 0.4F + 0.8F));
		}
	}

	public void fireProjectile(Entity source) {
        if (source.level().isClientSide()) return;

        ServerLevel serverWorld = (ServerLevel) source.level();
        float yaw = source.getYRot();
        float pitch = source.getXRot();

        Optional<Entity> opt$entityToSpawn = MiscUtil.getEntityWithPassengers(
                serverWorld,
                this.entityType(),
                this.tag(),
                source.position().add(0, source.getEyeHeight(source.getPose()), 0),
                yaw,
                pitch
        );
        if (opt$entityToSpawn.isEmpty()) return;

        Entity entity = opt$entityToSpawn.get();

		Vec3 rotationVector = entity.getLookAngle();

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
            RandomSource random = serverWorld.getRandom();
			Vec3 vec3d = (new Vec3(f, g, h)).normalize().add(random.nextGaussian() * 0.007499999832361937D * (double) this.divergence(), random.nextGaussian() * 0.007499999832361937D * (double) this.divergence(), random.nextGaussian() * 0.007499999832361937D * (double) this.divergence()).scale(this.speed());
			entity.setDeltaMovement(vec3d);
			Vec3 entityVelo = source.getDeltaMovement();
			entity.setDeltaMovement(entity.getDeltaMovement().add(entityVelo.x, source.onGround() ? 0.0D : entityVelo.y, entityVelo.z));
		}
		if (this.tag != null) {
			CompoundTag mergedTag = entity.saveWithoutId(new CompoundTag());
			mergedTag.merge(this.tag);
			entity.load(mergedTag);
		}
		serverWorld.tryAddFreshEntityWithPassengers(entity);
        ConfiguredEntityAction.execute(this.projectileAction(), entity);
        ConfiguredEntityAction.execute(this.shooterAction(), source);
	}

	@Override
	public int duration() {
		return this.cooldown();
	}
}
