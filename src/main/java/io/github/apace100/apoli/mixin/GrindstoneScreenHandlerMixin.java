package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.PowerModifiedGrindstone;
import io.github.apace100.apoli.mixin.forge.GrindstoneMenuResultSlotAccessor;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyGrindstonePower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyGrindstoneConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneScreenHandlerMixin extends AbstractContainerMenu implements PowerModifiedGrindstone {

    @Shadow @Final private Container resultSlots;

    @Shadow(remap = false) private int xp;

    @Unique
    private Player apoli$cachedPlayer;

    @Unique
    private Optional<BlockPos> apoli$cachedPosition;

    @Unique
    private List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> apoli$appliedPowers = new ArrayList<>();

    protected GrindstoneScreenHandlerMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void cachePlayer(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo ci) {
        this.apoli$cachedPlayer = playerInventory.player;
        this.apoli$cachedPosition = context.evaluate((w, bp) -> bp);
    }

    /*
    We are not using the Forge events here as we are unable to get an output stack
    for either case where both events would be useful.
     */
    @Inject(method = "createResult", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyResult(CallbackInfo ci, ItemStack top, ItemStack bottom) {
        this.apoli$appliedPowers.clear();
        List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> applyingPowers = ModifyGrindstonePower.tryGetApplyingPowers(this.apoli$cachedPlayer, top, bottom, this.resultSlots.getItem(0), this.apoli$cachedPosition);
        if (applyingPowers.isEmpty()) return;
        this.apoli$appliedPowers = applyingPowers;
        ItemStack newOutput = ModifyGrindstonePower.tryCreateOutput(this.apoli$appliedPowers, this.apoli$cachedPlayer.level(), top, bottom, this.resultSlots.getItem(0));
        this.resultSlots.setItem(0, newOutput);
        this.xp = (int) ModifierUtil.applyModifiers(this.apoli$cachedPlayer, ModifyGrindstonePower.tryGetExperienceModifiers(applyingPowers), ((GrindstoneMenuResultSlotAccessor)this.getSlot(2)).invokeGetExperienceAmount(this.apoli$cachedPlayer.level()));
        this.broadcastChanges();
    }

    /**
     Likewise with {@link io.github.apace100.apoli.mixin.forge.AbstractContainerMenuMixin}, this is
     a longer form way of handling GrindstoneScreenHandlerOutputSlotMixin.
      */
    @Unique int apoli$cachedPIndex;

    @Inject(method = "quickMoveStack", at = @At("HEAD"))
    private void cacheMouseX(Player pPlayer, int pIndex, CallbackInfoReturnable<ItemStack> cir) {
        this.apoli$cachedPIndex = pIndex;
    }

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), ordinal = 1)
    private ItemStack handleGrindstoneLateActionsQuickMove(ItemStack original) {
        if (this.apoli$cachedPIndex == 2) {
            Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
            ModifyGrindstonePower.tryLateExecute(this.apoli$appliedPowers, this.apoli$cachedPlayer, newStack, this.apoli$cachedPosition);
            return newStack.getValue();
        }
        return original;
    }

    /*
    @Inject(method = "updateResult", at = @At("RETURN"))
    private void modifyResult(CallbackInfo ci) {
        ItemStack top = input.getStack(0);
        ItemStack bottom = input.getStack(1);
        ItemStack output = result.getStack(0);
        List<ModifyGrindstonePower> applyingPowers = PowerHolderComponent.getPowers(apoli$cachedPlayer, ModifyGrindstonePower.class);
        applyingPowers = applyingPowers.stream().filter(mgp -> mgp.doesApply(top, bottom, output, apoli$cachedPosition)).toList();
        ItemStack newOutput = output;
        for(ModifyGrindstonePower mgp : applyingPowers) {
            newOutput = mgp.getOutput(top, bottom, newOutput);
        }
        apoli$appliedPowers = applyingPowers;
        result.setStack(0, newOutput);
        this.sendContentUpdates();
    }
     */

    @Override
    public List<Holder<ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower>>> getAppliedPowers() {
        return this.apoli$appliedPowers;
    }

    @Override
    public Player getPlayer() {
        return this.apoli$cachedPlayer;
    }

    @Override
    public Optional<BlockPos> getPos() {
        return this.apoli$cachedPosition;
    }
}