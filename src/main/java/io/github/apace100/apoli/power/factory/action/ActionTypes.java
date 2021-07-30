package io.github.apace100.apoli.power.factory.action;

import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class ActionTypes {

    public static ActionType<Entity> ENTITY = new ActionType<>("EntityAction", ApoliRegistries.ENTITY_ACTION);
    public static ActionType<ItemStack> ITEM = new ActionType<>("ItemAction", ApoliRegistries.ITEM_ACTION);
    public static ActionType<Triple<Level, BlockPos, Direction>> BLOCK = new ActionType<>("BlockAction", ApoliRegistries.BLOCK_ACTION);

}
