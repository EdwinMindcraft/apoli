package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.common.power.ActionOnItemUsePower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnItemUseConfiguration;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleSetCarriedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundSetCarriedItemPacket;getSlot()I", ordinal = 0))
    private void callActionOnUseStopBySwitching(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
        if(player.isUsingItem()) {
            ActionOnItemUsePower.execute(player, player.getUseItem(), VariableAccess.hand(player, player.getUsedItemHand()), ActionOnItemUseConfiguration.TriggerType.STOP, ActionOnItemUseConfiguration.PriorityPhase.ALL);
        }
    }

    @Inject(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;stopUsingItem()V"))
    private void callActionOnUseStopBySwappingHands(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if(player.isUsingItem()) {
            ActionOnItemUsePower.execute(player, player.getUseItem(), VariableAccess.hand(player, player.getUsedItemHand()), ActionOnItemUseConfiguration.TriggerType.STOP, ActionOnItemUseConfiguration.PriorityPhase.ALL);
        }
    }
}
