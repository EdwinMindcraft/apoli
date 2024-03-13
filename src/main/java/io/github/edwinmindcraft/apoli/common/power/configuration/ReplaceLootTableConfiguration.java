package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public record ReplaceLootTableConfiguration(Map<String, ResourceLocation> replacements, int priority,
                                            Holder<ConfiguredItemCondition<?, ?>> itemCondition, Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
                                            Holder<ConfiguredBlockCondition<?, ?>> blockCondition) implements IDynamicFeatureConfiguration {
    private static final SerializableDataType<Map<String, ResourceLocation>> REPLACEMENTS_DATA_TYPE = new SerializableDataType<>(ClassUtil.castClass(Map.class),
            (packetByteBuf, stringIdentifierMap) -> {
                packetByteBuf.writeInt(stringIdentifierMap.size());
                stringIdentifierMap.forEach(((s, identifier) -> {
                    packetByteBuf.writeUtf(s);
                    packetByteBuf.writeResourceLocation(identifier);
                }));
            },
            packetByteBuf -> {
                int count = packetByteBuf.readInt();
                Map<String, ResourceLocation> map = new LinkedHashMap<>();
                for(int i = 0;i < count; i++) {
                    String s = packetByteBuf.readUtf();
                    ResourceLocation id = packetByteBuf.readResourceLocation();
                    map.put(s, id);
                }
                return map;
            }, jsonElement -> {
        if(jsonElement.isJsonObject()) {
            JsonObject jo = jsonElement.getAsJsonObject();
            Map<String, ResourceLocation> map = new LinkedHashMap<>();
            for(String s : jo.keySet()) {
                JsonElement ele = jo.get(s);
                if(!ele.isJsonPrimitive()) {
                    continue;
                }
                JsonPrimitive jp = ele.getAsJsonPrimitive();
                if(!jp.isString()) {
                    continue;
                }
                ResourceLocation id = new ResourceLocation(jp.getAsString());
                map.put(s, id);
            }
            return map;
        }
        throw new JsonParseException("Expected a JSON object");
    });

    public static final Codec<ReplaceLootTableConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            REPLACEMENTS_DATA_TYPE.fieldOf("replace").forGetter(ReplaceLootTableConfiguration::replacements),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "priority", 0).forGetter(ReplaceLootTableConfiguration::priority),
            ConfiguredItemCondition.optional("item_condition").forGetter(ReplaceLootTableConfiguration::itemCondition),
            ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(ReplaceLootTableConfiguration::biEntityCondition),
            ConfiguredBlockCondition.optional("block_condition").forGetter(ReplaceLootTableConfiguration::blockCondition)
    ).apply(instance, ReplaceLootTableConfiguration::new));

    public boolean hasReplacement(ResourceLocation id) {
        String idString = id.toString();
        if(replacements().containsKey(idString)) {
            return true;
        }
        return replacements().keySet().stream().anyMatch(idString::matches);
    }

    public boolean doesApply(LootContext lootContext, Entity entity) {
        if(!ConfiguredBiEntityCondition.check(biEntityCondition(), entity, lootContext.getParam(LootContextParams.THIS_ENTITY))) {
            return false;
        }
        if(lootContext.hasParam(LootContextParams.TOOL)
                && !ConfiguredItemCondition.check(itemCondition(), lootContext.getLevel(), lootContext.getParam(LootContextParams.TOOL))) {
            return false;
        }
        if(lootContext.hasParam(LootContextParams.ORIGIN)) {
            BlockPos blockPos = new BlockPos(lootContext.getParam(LootContextParams.ORIGIN));
            return ConfiguredBlockCondition.check(blockCondition(), lootContext.getLevel(), blockPos);
        }
        return true;
    }

    @Nullable
    public ResourceLocation getReplacement(ResourceLocation id) {
        String idString = id.toString();
        if(replacements.containsKey(idString)) {
            return replacements.get(idString);
        }
        Set<String> keys = replacements.keySet();
        for(String s : keys) {
            if(idString.matches(s)) {
                return replacements.get(s);
            }
        }
        return null;
    }
}
