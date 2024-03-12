package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.common.power.ReplaceLootTablePower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LootTables.class)
public abstract class LootManagerMixin {

    @Shadow
    private Map<ResourceLocation, LootTable> tables;

    @Shadow public abstract LootTable get(ResourceLocation id);

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void setTableId(ResourceLocation id, CallbackInfoReturnable<LootTable> cir) {
        if (id.equals(ReplaceLootTablePower.REPLACED_TABLE_UTIL_ID)) {
            LootTable replace = ReplaceLootTablePower.peek();
            Apoli.LOGGER.info("Replacing " + id + " with " + ((IdentifiedLootTable)replace).getId());
            cir.setReturnValue(replace);
            //cir.setReturnValue(getTable(ReplaceLootTablePower.LAST_REPLACED_TABLE_ID));
        } else if (this.tables.containsKey(id)) {
            LootTable table = this.tables.get(id);
            if(table instanceof IdentifiedLootTable identifiedLootTable) {
                identifiedLootTable.setId(id, (LootTables)(Object)this);
            }
        }
    }
}