package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    /*
    This is done exclusively when the destroyProgress is 0.0 or less, which handles breaking blocks
    that are unbreakable. This method is the same as Origins Fabric.

    I don't think that I'm able to handle it within the forge event, so it has to go here instead.
     */
    @Inject(method = "getDestroyProgress", at = @At(value = "RETURN"), cancellable = true)
    private void allowUnbreakableBreaking(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Float> cir) {
        if (pState.getDestroySpeed(pLevel, pPos) <= 0.0F) {
            float base = cir.getReturnValue();
            float modified = PowerContainer.modify(pPlayer, ApoliPowers.MODIFY_BREAK_SPEED.get(), base, p -> ConfiguredBlockCondition.check(p.value().getConfiguration().condition(), pPlayer.level(), pPos, () -> pState));
            cir.setReturnValue(modified);
        }
    }
}
