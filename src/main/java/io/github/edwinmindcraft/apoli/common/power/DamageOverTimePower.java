package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.DamageOverTimeConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class DamageOverTimePower extends PowerFactory<DamageOverTimeConfiguration> {
	public DamageOverTimePower() {
		super(DamageOverTimeConfiguration.CODEC);
		this.ticking(true);
	}

	protected DataContainer getDataContainer(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, @Nullable PowerContainer player) {
		if (player == null)
			return new DataContainer(0, 0);
		return configuration.getPowerData(player, () -> new DataContainer(this.getDamageBegin(configuration.getConfiguration(), player.getOwner()), 0));
	}

	protected DataContainer getDataContainer(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, Entity player) {
		return configuration.getPowerData(player, () -> new DataContainer(this.getDamageBegin(configuration.getConfiguration(), player), 0));
	}

	@Override
	public void tick(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, Entity player) {
		if (configuration.isActive(player))
			this.doDamage(configuration, player);
		else
			this.resetDamage(configuration, player);
	}

	protected void doDamage(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, Entity player) {
		DataContainer dataContainer = this.getDataContainer(configuration, player);
		dataContainer.outOfDamageTicks = 0;
		if (dataContainer.inDamageTicks <= 0) {
			dataContainer.inDamageTicks = configuration.getConfiguration().interval();
			player.hurt(MiscUtil.createDamageSource(player.damageSources(), configuration.getConfiguration().damageSource(), configuration.getConfiguration().damageType()), player.level().getDifficulty() == Difficulty.EASY ? configuration.getConfiguration().damageEasy() : configuration.getConfiguration().damage());
		} else {
			--dataContainer.inDamageTicks;
		}
	}

	protected void resetDamage(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, Entity player) {
		DataContainer dataContainer = this.getDataContainer(configuration, player);
		if (dataContainer.outOfDamageTicks >= 20)
			dataContainer.inDamageTicks = this.getDamageBegin(configuration.getConfiguration(), player);
		else
			dataContainer.outOfDamageTicks++;
	}

	protected int getDamageBegin(DamageOverTimeConfiguration configuration, Entity player) {
		int prot = this.getProtection(configuration, player);
		int delay = (int) (Math.pow(prot * 2, 1.3) * configuration.protectionEffectiveness());
		return configuration.delay() + delay * 20;
	}

	private int getProtection(DamageOverTimeConfiguration configuration, Entity entity) {
		if (configuration.protectionEnchantment() == null) {
			return 0;
		} else {
			if (!(entity instanceof LivingEntity living))
				return 0;
			Map<EquipmentSlot, ItemStack> enchantedItems = configuration.protectionEnchantment().getSlotItems(living);
			Iterable<ItemStack> iterable = enchantedItems.values();
			int i = 0;
			int items = 0;
			for (ItemStack itemStack : iterable) {
				int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(configuration.protectionEnchantment(), itemStack);
				i += enchLevel;
				if (enchLevel > 0)
					items++;
			}
			return i + items;
		}
	}

	@Override
	public void onRespawn(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, Entity entity) {
		DataContainer dataContainer = this.getDataContainer(configuration, entity);
		dataContainer.inDamageTicks = 0;
		dataContainer.outOfDamageTicks = 0;
	}

	@Override
	public void serialize(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		DataContainer dataContainer = this.getDataContainer(configuration, container);
		tag.putInt("InDamage", dataContainer.inDamageTicks);
		tag.putInt("OutDamage", dataContainer.outOfDamageTicks);
	}

	@Override
	public void deserialize(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		DataContainer dataContainer = this.getDataContainer(configuration, container);
		dataContainer.inDamageTicks = tag.getInt("InDamage");
		dataContainer.outOfDamageTicks = tag.getInt("OutDamage");
	}

	private static final class DataContainer {
		private int inDamageTicks;
		private int outOfDamageTicks;

		private DataContainer(int inDamageTicks, int outOfDamageTicks) {
			this.inDamageTicks = inDamageTicks;
			this.outOfDamageTicks = outOfDamageTicks;
		}
	}
}
