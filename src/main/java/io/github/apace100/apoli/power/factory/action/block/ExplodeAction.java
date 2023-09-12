package io.github.apace100.apoli.power.factory.action.block;

import io.github.apace100.apoli.action.configuration.ExplodeConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDefaultConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ExplodeAction extends BlockAction<ExplodeConfiguration> {


	public ExplodeAction() {
		super(ExplodeConfiguration.CODEC);
	}

	@Override
	public void execute(@NotNull ExplodeConfiguration configuration, @NotNull Level world, @NotNull BlockPos pos, @NotNull Direction direction) {
		if (world.isClientSide())
			return;
		ExplosionDamageCalculator calculator = !configuration.indestructible().is(ApoliDefaultConditions.BLOCK_DEFAULT.getId()) ? configuration.calculator() : null;
        explode(world, null, world.damageSources().explosion(null), calculator, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, configuration.power(), configuration.createFire(), configuration.destructionType());
    }

    private static void explode(Level world, Entity entity, DamageSource damageSource, ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType) {
        Explosion explosion = new Explosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }

	/*public static ActionFactory<Triple<World, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(Apoli.identifier("explode"),
				new SerializableData()
						.add("power", SerializableDataTypes.FLOAT)
						.add("destruction_type", SerializableDataType.enumValue(Explosion.DestructionType.class), Explosion.DestructionType.BREAK)
						.add("damage_self", SerializableDataTypes.BOOLEAN, true)
						.add("indestructible", ApoliDataTypes.BLOCK_CONDITION, null)
						.add("destructible", ApoliDataTypes.BLOCK_CONDITION, null)
						.add("create_fire", SerializableDataTypes.BOOLEAN, false),
				ExplodeAction::action
		);
	}*/
}
