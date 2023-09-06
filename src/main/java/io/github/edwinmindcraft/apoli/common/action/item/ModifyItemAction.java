package io.github.edwinmindcraft.apoli.common.action.item;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.Mutable;

public class ModifyItemAction extends ItemAction<FieldConfiguration<ResourceLocation>> {

	public ModifyItemAction() {
		super(FieldConfiguration.codec(SerializableDataTypes.IDENTIFIER, "modifier"));
	}

	@Override
	public void execute(FieldConfiguration<ResourceLocation> configuration, Level level, Mutable<ItemStack> stack) {
		MinecraftServer server = level.getServer();
		if (server != null) {
			LootDataManager lootFunctionManager = server.getLootData();
			LootItemFunction lootFunction = lootFunctionManager.getElement(LootDataType.MODIFIER, configuration.value());
			if (lootFunction == null) {
				Apoli.LOGGER.info("Unknown item modifier used in `modify` action: " + configuration.value());
				return;
			}
            LootParams lootContextParameterSet = new LootParams.Builder(server.overworld()).withParameter(LootContextParams.ORIGIN, new Vec3(0, 0,0)).create(LootContextParamSets.COMMAND);
            LootContext lootContext = new LootContext.Builder(lootContextParameterSet).create(null);
            ItemStack newStack = lootFunction.apply(stack.getValue(), lootContext);
            stack.setValue(newStack);
        }
	}
}
