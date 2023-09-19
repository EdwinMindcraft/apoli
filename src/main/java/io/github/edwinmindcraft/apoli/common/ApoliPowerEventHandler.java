package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.apace100.apoli.util.StackPowerUtil;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.InteractionPowerConfiguration;
import io.github.edwinmindcraft.apoli.common.power.*;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageDealtConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageTakenConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.LivingDamageCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupScheduler;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = Apoli.MODID)
public class ApoliPowerEventHandler {
	@SubscribeEvent
	public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		Level world = player.getCommandSenderWorld();
		BlockState state = event.getState();
		Optional<BlockPos> position = event.getPosition();
		if (position.isEmpty())
			return;
		float hardness = state.getDestroySpeed(world, position.get());
		if (hardness <= 0)
			return;
		float speed = event.getNewSpeed();
		//This is less than ideal since it fires two hooks, but hey, I do what I can.
		boolean stateCheck = state.canHarvestBlock(event.getEntity().getLevel(), position.get(), event.getEntity());
		boolean forgeCheck = ForgeHooks.isCorrectToolForDrops(state, event.getEntity());
		int toolFactor = forgeCheck ? 30 : 100;
		float factor = hardness * toolFactor;
		speed = IPowerContainer.modify(player, ApoliPowers.MODIFY_BREAK_SPEED.get(), speed / factor, p -> ConfiguredBlockCondition.check(p.value().getConfiguration().condition(), world, position.get(), () -> state)) * factor;

		if (stateCheck == forgeCheck)
			event.setNewSpeed(speed);
		else if (stateCheck) //State returned true, forge returned false >> Speed *= 100 / 30
			event.setNewSpeed(speed * 3.3333333f);
		else //State returned false, forge returned true >> Speed *= 30 / 100
			event.setNewSpeed(speed * 0.3f);
		if (speed <= 0)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		//FIXME I'm not sure this will work properly.
		if (PreventSleepPower.tryPreventSleep(event.getEntity(), event.getEntity().getCommandSenderWorld(), event.getPos()))
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
	}

	@SubscribeEvent
	public static void onWakeUp(PlayerWakeUpEvent event) {
		if (!event.wakeImmediately() && !event.updateLevel())
			event.getEntity().getSleepingPos().ifPresent(pos -> ActionOnWakeUpPower.execute(event.getEntity(), pos));
	}

	@SubscribeEvent
	public static void finishUsing(LivingEntityUseItemEvent.Finish event) {
		ActionOnItemUsePower.execute(event.getEntity(), event.getItem(), new VariableAccess<>(event::getResultStack, event::setResultStack));
	}

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent event) {
		if (event.getPlayer() instanceof ServerPlayer)
			ActionOnBlockBreakPower.execute(event.getPlayer(), event.getLevel(), event.getPos(), event::getState, !event.isCanceled());
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		LivingEntity entityLiving = event.getEntity();
		//TODO Check if this works. It should since MC1.17 seems to fire this on both sides.
		ActionOnLandPower.execute(entityLiving);
		if (IPowerContainer.getPowers(entityLiving, ApoliPowers.MODIFY_FALLING.get()).stream().anyMatch(x -> !x.value().getConfiguration().takeFallDamage()))
			event.setDamageMultiplier(0.0F); //Disable fall damage without actually removing distance. This is to avoid breaking compatibility.
	}

	private static float computeDamageAmount(Entity target, DamageSource source, float amount, boolean exec) {
		Entity sourceEntity = source.getEntity();
		if (sourceEntity != null) {
			Consumer<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> otherConsumer = exec ?
					x -> x.value().getFactory().execute(x.value(), sourceEntity, target) :
					x -> { };
			ModifyDamageDealtPower factory = source.isProjectile() ? ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get() : ApoliPowers.MODIFY_DAMAGE_DEALT.get();
			if (target instanceof LivingDamageCache pdc && pdc.getModifyDamageDealtPowers() != null)
				amount = (float) IPowerContainer.modify(sourceEntity, factory, pdc.getModifyDamageDealtPowers(), amount, otherConsumer);
			else {
				float finalAmount = amount;
				amount = IPowerContainer.modify(sourceEntity, factory, amount, x -> factory.check(x.value(), sourceEntity, target, source, finalAmount), otherConsumer);
			}
		}
		Consumer<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> selfConsumer = exec ?
				x -> x.value().getFactory().execute(x.value(), target, source) :
				x -> { };
		if (target instanceof LivingDamageCache pdc && pdc.getModifyDamageTakenPowers() != null) {
			amount = (float) IPowerContainer.modify(sourceEntity, ApoliPowers.MODIFY_DAMAGE_TAKEN.get(), pdc.getModifyDamageTakenPowers(), amount, selfConsumer);
			if (amount > 0) pdc.bypassDamageCheck(true);
		} else {
			float finalAmount = amount;
			amount = IPowerContainer.modify(sourceEntity, ApoliPowers.MODIFY_DAMAGE_TAKEN.get(), amount, x -> x.value().getFactory().check(x.value(), target, source, finalAmount), selfConsumer);
		}
		return amount;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void livingDamage(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource source = event.getSource();
		float amount = event.getAmount();
		event.setAmount(computeDamageAmount(target, source, amount, false));
		if (event.getAmount() != amount && event.getAmount() <= 0)
			event.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void livingAttack(LivingAttackEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource source = event.getSource();
		float amount = event.getAmount();
		if (target instanceof LivingDamageCache ldc)
			ldc.gatherDamagePowers(target, source, amount);
		IPowerDataCache.get(target).ifPresent(x -> x.setDamage(amount));
		float newAmount = computeDamageAmount(target, source, amount, true);

		if (newAmount != amount && newAmount <= 0)
			event.setCanceled(true);
	}

	/**
	 * This needs to be executed after COMBAT's jump overhaul.
	 */
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void livingJump(LivingEvent.LivingJumpEvent event) {
		double modified = ModifyJumpPower.apply(event.getEntity(), event.getEntity().getDeltaMovement().y);
		updateJumpHeight(modified, event.getEntity());
	}

	private static void updateJumpHeight(double height, LivingEntity entity) {
		Vec3 vel = entity.getDeltaMovement();
		double delta = height - vel.y;
		if (delta == 0)
			return;
		entity.setDeltaMovement(vel.add(0, delta, 0));
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		float amount = event.getAmount();
		LazyOptional<IPowerDataCache> pdc = IPowerDataCache.get(target);
		//This is only true if the invulnerability path was used. (LivingEntity:1102)
		//Using this allows me to bypass an inconsistency in the vanilla code.
		boolean validate = event.getAmount() != target.lastHurt;
		float prevDamage = pdc.map(IPowerDataCache::getDamage).orElse(Float.POSITIVE_INFINITY);
		pdc.ifPresent(x -> x.setDamage(amount));
		if (amount > 0 && !event.isCanceled() && (!validate || target.invulnerableTime > 10F || prevDamage <= amount)) {
			SelfActionWhenHitPower.execute(target, source, amount);
			AttackerActionWhenHitPower.execute(target, source, amount);
			if (attacker != null) {
				SelfCombatActionPower.onHit(attacker, target, source, amount);
				TargetCombatActionPower.onHit(attacker, target, source, amount);
				CombatHitActionPower.perform(attacker, target, source, amount);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingKilled(LivingDeathEvent event) {
		IPowerDataCache.get(event.getEntity()).map(IPowerDataCache::getDamage).ifPresent(x -> {
			if (event.getSource().getEntity() instanceof LivingEntity living)
				SelfCombatActionPower.onKill(living, event.getEntity(), event.getSource(), x);
		});
	}

	@SubscribeEvent
	public static void updateHealAmount(LivingHealEvent event) {
		event.setAmount(IPowerContainer.modify(event.getEntity(), ApoliPowers.MODIFY_HEALING.get(), event.getAmount()));
	}

    @SubscribeEvent
    public static void playerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ServerPlayer spe && ApoliAPI.getPowerContainer(spe) != null && ApoliAPI.getPowerContainer(spe).hasPower(ApoliPowers.MODIFY_PLAYER_SPAWN.get())) {
            ApoliPowers.MODIFY_PLAYER_SPAWN.get().tick(spe);
        }
    }

    @SubscribeEvent
    public static void onCalioReload(CalioDynamicRegistryEvent.Reload event) throws InterruptedException {
        SpawnLookupScheduler.INSTANCE.clear();
    }

    @SubscribeEvent
    public static void onStopServer(ServerStoppingEvent event) throws InterruptedException {
        // Reset the cache on integrated servers.
        SpawnLookupScheduler.INSTANCE.clear();
    }

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof Player player) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer && IPowerContainer.hasPower(serverPlayer, ApoliPowers.MODIFY_PLAYER_SPAWN.get()))
                ApoliPowers.MODIFY_PLAYER_SPAWN.get().checkSpawn(serverPlayer);

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                IPowerContainer.getPowers(player, ApoliPowers.INVENTORY.get()).stream().map(Holder::value).forEach(inventory -> {
                    InventoryPower.tryDropItemsOnDeath(inventory, player);
                });
                IPowerContainer.getPowers(player, ApoliPowers.KEEP_INVENTORY.get()).forEach(power -> power.value().getFactory().captureItems(power.value(), player));
            }
		}
	}

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		List<Component> tooltips = event.getToolTip();
		int flags = event.getItemStack().hasTag() &&
					Objects.requireNonNull(event.getItemStack().getTag()).contains("HideFlags", 99) ?
				event.getItemStack().getTag().getInt("HideFlags") :
				event.getItemStack().getItem().getDefaultTooltipHideFlags(event.getItemStack());

		if (ApoliConfigs.CLIENT.tooltips.showUsabilityHints.get() && (flags & ItemStack.TooltipPart.ADDITIONAL.getMask()) == 0) {
			List<ConfiguredPower<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>, PreventItemActionPower>> powers = new ArrayList<>(PreventItemActionPower.getPreventingForDisplay(event.getEntity(), event.getItemStack()));
			int size = powers.size();
			if (!powers.isEmpty()) {
				powers.removeIf(x -> x.getData().hidden());
				String key = "tooltip.apoli.unusable." + event.getItemStack().getUseAnimation().name().toLowerCase(Locale.ROOT);
				ChatFormatting textColor = ChatFormatting.GRAY;
				ChatFormatting powerColor = ChatFormatting.RED;
				if (ApoliConfigs.CLIENT.tooltips.compactUsabilityHints.get() || powers.size() == 0) {
					if (powers.size() == 1) {
						ConfiguredPower<?, ?> power = powers.get(0);
						tooltips.add(Component.translatable(key + ".single", power.getData().getName().withStyle(powerColor)).withStyle(textColor));
					} else {
						tooltips.add(Component.translatable(key + ".multiple", Component.literal((powers.size() == 0 ? size : powers.size()) + "").withStyle(powerColor)).withStyle(textColor));
					}
				} else {
					MutableComponent powerNameList = powers.get(0).getData().getName().withStyle(powerColor);
					for (int i = 1; i < powers.size(); i++) {
						powerNameList = powerNameList.append(Component.literal(", ").withStyle(textColor));
						powerNameList = powerNameList.append(powers.get(i).getData().getName().withStyle(powerColor));
					}
					MutableComponent preventText = Component.translatable(key + ".single", powerNameList).withStyle(textColor);
					tooltips.add(preventText);
				}
			}
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			List<StackPowerUtil.StackPower> powers = StackPowerUtil.getPowers(event.getItemStack(), slot)
					.stream()
					.filter(sp -> !sp.isHidden)
					.toList();
			if (powers.size() > 0) {
				tooltips.add(Component.empty());
				tooltips.add(Component.translatable("item.modifiers." + slot.getName()).withStyle(ChatFormatting.GRAY));
				powers.forEach(sp -> {
					if (PowerTypeRegistry.contains(sp.powerId)) {
						PowerType<?> powerType = PowerTypeRegistry.get(sp.powerId);
						tooltips.add(Component.literal(" ").append(powerType.getName()).withStyle(sp.isNegative ? ChatFormatting.RED : ChatFormatting.BLUE));
						if (event.getFlags().isAdvanced())
							tooltips.add(Component.literal("  ").append(powerType.getDescription()).withStyle(ChatFormatting.GRAY));
					}
				});
			}
		}
		TooltipPower.tryAdd(event.getEntity(), event.getItemStack(), event.getToolTip());
	}

	//If the interaction isn't canceled, let other mod interaction play, as this can cancel interactions.
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void playerEntityInteraction(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		if (player.isSpectator()) return;
		Entity target = event.getTarget();
		Optional<InteractionResult> result = Stream.concat(
				ActionOnEntityUsePower.tryInteract(player, target, event.getHand()).stream(),
				ActionOnBeingUsedPower.tryInteract(target, player, event.getHand()).stream()).reduce(InteractionPowerConfiguration::reduce);
		result.ifPresent(res -> {
			if (res != InteractionResult.PASS) {
				event.setCancellationResult(res);
				event.setCanceled(true);
			}
		});
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void playerBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
		ActionOnBlockUsePower.tryInteract(event.getEntity(), event.getPos(), event.getFace(), event.getHand()).ifPresent(res -> {
			event.setCancellationResult(res);
			event.setCanceled(true);
		});
	}

	//region Prevention Block
	//This blocks runs on EventPriority.HIGHEST as cancelling should be final in those cases.

	@SubscribeEvent
	public static void preventPotionEffect(MobEffectEvent.Applicable event) {
		if (EffectImmunityPower.isImmune(event.getEntity(), event.getEffectInstance()))
			event.setResult(Event.Result.DENY);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void preventGameEvent(VanillaGameEvent event) {
		if (event.getCause() != null && PreventGameEventPower.tryPreventGameEvent(event.getCause(), event.getVanillaEvent()))
			event.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void makeInvulnerable(LivingAttackEvent event) {
		LivingEntity entityLiving = event.getEntity();
		if (InvulnerablePower.isInvulnerableTo(entityLiving, event.getSource()))
			event.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void preventLivingDeath(LivingDeathEvent event) {
		//I don't know at what priority I should fire this, but as a precaution, I'm firing it at the highest priority,
		//as actions that happen when you die shouldn't actually happen if you survive.
		IPowerDataCache.get(event.getEntity()).map(IPowerDataCache::getDamage).ifPresent(x -> {
			if (PreventDeathPower.tryPreventDeath(event.getEntity(), event.getSource(), x)) {
				event.getEntity().setHealth(1.0F);
				event.setCanceled(true);
			}
		});
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void preventEntityInteraction(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		if (player.isSpectator()) return;
		Entity target = event.getTarget();
		ActionOnEntityUsePower.tryPrevent(player, target, event.getHand())
				.or(() -> ActionOnBeingUsedPower.tryPrevent(target, player, event.getHand())).ifPresent(res -> {
					event.setCancellationResult(res);
					event.setCanceled(true);
				});
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void preventBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
		if (PreventBlockActionPower.isUsagePrevented(event.getEntity(), event.getPos()))
			event.setUseBlock(Event.Result.DENY);
		//Allow food restricted origins to plant carrots and potatoes
		if (!(event.getItemStack().getItem() instanceof BlockItem) && PreventItemActionPower.isUsagePrevented(event.getEntity(), event.getItemStack()))
			event.setUseItem(Event.Result.DENY);
		if (event.getItemStack().getItem() instanceof ArmorItem) {
			EquipmentSlot slot = Mob.getEquipmentSlotForItem(event.getItemStack());
			if (RestrictArmorPower.isForbidden(event.getEntity(), slot, event.getItemStack()))
				event.setUseItem(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void preventItemUsage(PlayerInteractEvent.RightClickItem event) {
		if (PreventItemActionPower.isUsagePrevented(event.getEntity(), event.getItemStack()))
			event.setCanceled(true);
		if (event.getItemStack().getItem() instanceof ArmorItem) {
			EquipmentSlot slot = Mob.getEquipmentSlotForItem(event.getItemStack());
			if (RestrictArmorPower.isForbidden(event.getEntity(), slot, event.getItemStack()))
				event.setCanceled(true);
		}
	}

	//endregion
}
