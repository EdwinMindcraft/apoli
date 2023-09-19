package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConfirmScreen.class)
public class ConfirmScreenMixin extends Screen {
    @Shadow private int delayTicker;
    @Shadow @Final private List<Button> exitButtons;
    @Unique
    boolean apoli$previouslyInactive = false;

    protected ConfirmScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void waitForRespawnPoint(CallbackInfo ci) {
        if (this.minecraft.player != null && ((ModifyPlayerSpawnCache)this.minecraft.player).getActiveSpawnPower() != null && this.delayTicker <= 0) {
            if (!this.apoli$previouslyInactive && !SpawnLookupUtil.hasSpawnCached(((ModifyPlayerSpawnCache)this.minecraft.player).getActiveSpawnPower())) {
                // Set the Respawn button to be inactive.
                this.exitButtons.get(1).active = false;
                this.apoli$previouslyInactive = true;
            } else if (this.apoli$previouslyInactive && SpawnLookupUtil.hasSpawnCached(((ModifyPlayerSpawnCache)this.minecraft.player).getActiveSpawnPower())) {
                // Set the Respawn button to be active again once the spawn has been found.
                this.exitButtons.get(1).active = true;
                this.apoli$previouslyInactive = false;
                ((ModifyPlayerSpawnCache)this.minecraft.player).removeActiveSpawnPower();
            }
        }
    }

}
