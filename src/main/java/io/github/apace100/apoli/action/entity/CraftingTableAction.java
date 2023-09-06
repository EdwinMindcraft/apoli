package io.github.apace100.apoli.action.entity;

import io.github.edwinmindcraft.apoli.common.action.entity.SimpleEntityAction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingTableAction extends SimpleEntityAction {
	private static final Component TITLE = Component.translatable("container.crafting");

	public static void action(Entity entity) {
		if (!(entity instanceof Player player)) return;

		player.openMenu(new SimpleMenuProvider((syncId, inventory, _player) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(_player.level(), _player.blockPosition())), TITLE));
		player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
	}

	public CraftingTableAction() {
		super(CraftingTableAction::action);
	}
}
