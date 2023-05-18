package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    /*
    Origins Fabric additionally injects into the clause of a -1.0F BlockState.getDestroySpeed.
    Whereas here we use Forge's break speed events, so a modify variable to modify the -1.0 into a 0.0, so it won't
    automatically return 0.0 will do.
    */
    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void allowUnbreakableBreaking(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Float> cir) {
        if (pState.getDestroySpeed(pLevel, pPos) <= 0.0F && IPowerContainer.hasPower(pPlayer, ApoliPowers.MODIFY_BREAK_SPEED.get())) {
            float base = cir.getReturnValue();
            float modified = IPowerContainer.modify(pPlayer, ApoliPowers.MODIFY_BREAK_SPEED.get(), base, p -> ConfiguredBlockCondition.check(p.getConfiguration().condition(), pPlayer.level, pPos, () -> pState));
            cir.setReturnValue(modified);
        }
    }
}
