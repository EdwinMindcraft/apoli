package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CActiveSpawnPowerPacket;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin {

    // As Forge's LivingDeathEvent does not account for the /kill command, we must do it ourselves.
    @Inject(method = "kill", at = @At("HEAD"))
    private void handleKillMethod(CallbackInfo ci) {
        if (((Entity)(Object)this) instanceof ServerPlayer serverPlayer && ((ModifyPlayerSpawnCache)serverPlayer).getActiveSpawnPower() != null) {
            ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new S2CActiveSpawnPowerPacket(Optional.of(((ModifyPlayerSpawnCache)serverPlayer).getActiveSpawnPower())));
        }
    }
}
