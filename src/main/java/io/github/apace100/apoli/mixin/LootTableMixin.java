package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.IdentifiedLootTable;
import io.github.apace100.apoli.access.ReplacingLootContext;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.power.ReplaceLootTablePower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ReplaceLootTableConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin implements IdentifiedLootTable {

    @Unique
    private ResourceLocation apoli$id;
    @Unique
    private LootDataManager apoli$lootManager;

    @Override
    public void setId(ResourceLocation id, LootDataManager lootManager) {
        apoli$id = id;
        apoli$lootManager = lootManager;
    }

    @Override
    public ResourceLocation getId() {
        return apoli$id;
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void modifyLootTable(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        if(((ReplacingLootContext)context).isReplaced((LootTable)(Object)this)) {
            return;
        }
        if(context.hasParam(LootContextParams.THIS_ENTITY)) {
            LootContextParamSet type = ((ReplacingLootContext)context).getType();
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
            if(type == LootContextParamSets.FISHING) {
                if(entity instanceof FishingHook bobber) {
                    entity = bobber.getPlayerOwner();
                }
            } else if(type == LootContextParamSets.ENTITY) {
                if(context.hasParam(LootContextParams.KILLER_ENTITY)) {
                    entity = context.getParam(LootContextParams.KILLER_ENTITY);
                }
            } else if(type == LootContextParamSets.PIGLIN_BARTER) {
                if(entity instanceof Piglin piglin) {
                    Optional<Player> optional = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
                    if(optional.isPresent()) {
                        entity = optional.get();
                    }
                }
            }
            List<ReplaceLootTableConfiguration> powers = IPowerContainer.getPowers(entity, ApoliPowers.REPLACE_LOOT_TABLE.get()).stream().map(holder -> holder.value().getConfiguration()).toList();
            Entity finalEntity = entity;
            powers = powers.stream()
                .filter(p -> p.hasReplacement(apoli$id) && p.doesApply(context, finalEntity))
                .sorted(Comparator.comparing(ReplaceLootTableConfiguration::priority))
                .toList();
            if(powers.size() == 0) {
                return;
            }
            ReplaceLootTablePower.addToStack((LootTable)(Object)this);
            LootTable replacement = null;
            for (ReplaceLootTableConfiguration power : powers) {
                ResourceLocation id = power.getReplacement(apoli$id);
                replacement = apoli$lootManager.getLootTable(id);
                ReplaceLootTablePower.addToStack(replacement);
            }
            ((ReplacingLootContext)context).setReplaced((LootTable)(Object)this);
            replacement.getRandomItemsRaw(context, lootConsumer);
            ReplaceLootTablePower.clearStack();
            ci.cancel();
        }
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;pushVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)Z"))
    private void popReplacementStack(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        ReplaceLootTablePower.pop();
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;popVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)V"))
    private void restoreReplacementStack(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        ReplaceLootTablePower.restore();
    }
}