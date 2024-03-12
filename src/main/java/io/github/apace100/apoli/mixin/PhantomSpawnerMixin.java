package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Unique
    private Player apoli$cachedPlayer;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cachePlayerEntity(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir, RandomSource random, int i, Iterator var6, ServerPlayer player, BlockPos blockpos) {
        apoli$cachedPlayer = player;
    }

    // This is imo a better way to handle this, so then the modified insomnia ticks will be properly clamped.
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 0)
    private int modifyTicks(int original) {
        if (ApoliAPI.getPowerContainer(apoli$cachedPlayer).hasPower(ApoliPowers.MODIFY_INSONMIA_TICKS.get())) {
            return (int) PowerContainer.modify(apoli$cachedPlayer, ApoliPowers.MODIFY_INSONMIA_TICKS.get(), original);
        }
        return original;
    }
}
