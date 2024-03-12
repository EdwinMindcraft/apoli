package io.github.apace100.apoli.mixin.forge;

import io.github.apace100.apoli.access.EntityAttributeInstanceAccess;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.power.ModifyFallingPower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.common.ForgeMod;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

// TODO: Move outside of Forge package for Origins 1.20.2.
@Mixin(AttributeInstance.class)
public abstract class EntityAttributeInstanceMixin implements EntityAttributeInstanceAccess {
    @Shadow
    @Final
    private Attribute attribute;

    @Unique
    @Nullable
    private Entity apoli$entity;

    @Override
    public void setEntity(Entity entity) {
        apoli$entity = entity;
    }

    @Override
    public @Nullable Entity getEntity() {
        return apoli$entity;
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void apoli$modifyAttributeValue(CallbackInfoReturnable<Double> cir) {
        if (apoli$entity != null && attribute == NeoForgeMod.ENTITY_GRAVITY.value() && apoli$entity.getDeltaMovement().y <= 0D && PowerContainer.hasPower(apoli$entity, ApoliPowers.MODIFY_FALLING.get())) {
            double original = cir.getReturnValueD();
            cir.setReturnValue(ModifyFallingPower.apply(apoli$entity, original));
        }
    }

}