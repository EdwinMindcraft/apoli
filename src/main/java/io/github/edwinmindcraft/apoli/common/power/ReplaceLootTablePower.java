package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ReplaceLootTableConfiguration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Stack;

public class ReplaceLootTablePower extends PowerFactory<ReplaceLootTableConfiguration> {
    public static final ResourceLocation REPLACED_TABLE_UTIL_ID = Apoli.identifier("replaced_loot_table");

    private static final Stack<LootTable> REPLACEMENT_STACK = new Stack<>();
    private static final Stack<LootTable> BACKTRACK_STACK = new Stack<>();

    public ReplaceLootTablePower() {
        super(ReplaceLootTableConfiguration.CODEC);
    }

    public static void clearStack() {
        REPLACEMENT_STACK.clear();
        BACKTRACK_STACK.clear();
    }

    public static void addToStack(LootTable lootTable) {
        REPLACEMENT_STACK.add(lootTable);
    }

    public static LootTable pop() {
        if(REPLACEMENT_STACK.isEmpty()) {
            return LootTable.EMPTY;
        }
        LootTable table = REPLACEMENT_STACK.pop();
        BACKTRACK_STACK.push(table);
        return table;
    }

    public static LootTable restore() {
        if(BACKTRACK_STACK.isEmpty()) {
            return LootTable.EMPTY;
        }
        LootTable table = BACKTRACK_STACK.pop();
        REPLACEMENT_STACK.push(table);
        return table;
    }

    public static LootTable peek() {
        if(REPLACEMENT_STACK.isEmpty()) {
            return LootTable.EMPTY;
        }
        return REPLACEMENT_STACK.peek();
    }

    private static void printStacks() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        int count = 0;
        while(!REPLACEMENT_STACK.isEmpty()) {
            LootTable t = pop();
            stringBuilder.append(t == null ? "null" : t.getLootTableId());
            if(!REPLACEMENT_STACK.isEmpty()) {
                stringBuilder.append(", ");
            }
            count++;
        }
        stringBuilder.append("], [");
        while(count > 0) {
            restore();
            count--;
        }
        while(BACKTRACK_STACK.size() > 0) {
            LootTable t = restore();
            stringBuilder.append(t == null ? "null" : t.getLootTableId());
            if(!BACKTRACK_STACK.isEmpty()) {
                stringBuilder.append(", ");
            }
            count++;
        }
        while(count > 0) {
            pop();
            count--;
        }
        stringBuilder.append("]");
        Apoli.LOGGER.info(stringBuilder.toString());
    }
}
