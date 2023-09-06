package io.github.apace100.apoli.util;

import com.google.gson.JsonSyntaxException;
import io.github.apace100.apoli.data.DamageSourceDescription;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MiscUtil {

	public static BlockState getInWallBlockState(LivingEntity playerEntity) {
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int i = 0; i < 8; ++i) {
			double d = playerEntity.getX() + (double) (((float) (i % 2) - 0.5F) * playerEntity.getBbWidth() * 0.8F);
			double e = playerEntity.getEyeY() + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
			double f = playerEntity.getZ() + (double) (((float) ((i >> 2) % 2) - 0.5F) * playerEntity.getBbWidth() * 0.8F);
			mutable.set(d, e, f);
			BlockState blockState = playerEntity.level().getBlockState(mutable);
			if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(playerEntity.level(), mutable)) {
				return blockState;
			}
		}

		return null;
	}

    /*
    public static <T> Predicate<T> combineOr(Predicate<T> a, Predicate<T> b) {
        if(a == null) {
            return b;
        }
        if(b == null) {
            return a;
        }
        return a.or(b);
    }

    public static <T> Predicate<T> combineAnd(Predicate<T> a, Predicate<T> b) {
        if(a == null) {
            return b;
        }
        if(b == null) {
            return a;
        }
        return a.and(b);
    }
     */

    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  Optional<DamageSourceDescription> damageSourceDescription,
                                                  Optional<ResourceKey<DamageType>> damageType) {
        if(damageSourceDescription.isEmpty() && damageType.isEmpty()) {
            throw new JsonSyntaxException("Either a legacy damage source or an ID of a damage type must be specified");
        }
        return damageSourceDescription.isEmpty() ? damageSources.source(damageType.get()) : damageSourceDescription.get().create(damageSources);
    }

    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  Optional<DamageSourceDescription> damageSourceDescription,
                                                  Optional<ResourceKey<DamageType>> damageType, Entity attacker) {
        if(damageSourceDescription.isEmpty() && damageType.isEmpty()) {
            throw new JsonSyntaxException("Either a legacy damage source or an ID of a damage type must be specified");
        }
        return damageSourceDescription.isEmpty() ? damageSources.source(damageType.get(), attacker) : damageSourceDescription.get().create(damageSources, attacker);
    }

    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  Optional<DamageSourceDescription> damageSourceDescription,
                                                  Optional<ResourceKey<DamageType>> damageType, Entity source, Entity attacker) {
        if(damageSourceDescription.isEmpty() && damageType.isEmpty()) {
            throw new JsonSyntaxException("Either a legacy damage source or an ID of a damage type must be specified");
        }
        return damageSourceDescription.isEmpty() ? damageSources.source(damageType.get(), source, attacker) : damageSourceDescription.get().create(damageSources, source, attacker);
    }

    //region Xplatform methods
    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  @Nullable DamageSourceDescription damageSourceDescription,
                                                  @Nullable ResourceKey<DamageType> damageType) {
        return createDamageSource(damageSources, Optional.ofNullable(damageSourceDescription), Optional.ofNullable(damageType));
    }

    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  @Nullable DamageSourceDescription damageSourceDescription,
                                                  @Nullable ResourceKey<DamageType> damageType, Entity attacker) {
        return createDamageSource(damageSources, Optional.ofNullable(damageSourceDescription), Optional.ofNullable(damageType), attacker);
    }

    public static DamageSource createDamageSource(DamageSources damageSources,
                                                  @Nullable DamageSourceDescription damageSourceDescription,
                                                  @Nullable ResourceKey<DamageType> damageType, Entity source, Entity attacker) {
        return createDamageSource(damageSources, Optional.ofNullable(damageSourceDescription), Optional.ofNullable(damageType), source, attacker);
    }
    //endregion
}
