package io.github.apace100.apoli.power.factory.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.apace100.apoli.util.NamespaceAlias;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ConditionType<T> {

    private final String conditionTypeName;
    private final Registry<ConditionFactory<T>> conditionRegistry;

    public ConditionType(String conditionTypeName, Registry<ConditionFactory<T>> conditionRegistry) {
        this.conditionTypeName = conditionTypeName;
        this.conditionRegistry = conditionRegistry;
    }

    public void write(FriendlyByteBuf buf, ConditionFactory.Instance conditionInstance) {
        conditionInstance.write(buf);
    }

    public ConditionFactory<T>.Instance read(FriendlyByteBuf buf) {
        ResourceLocation type = ResourceLocation.tryParse(buf.readUtf(32767));
        ConditionFactory<T> conditionFactory = conditionRegistry.get(type);
        return conditionFactory.read(buf);
    }

    public ConditionFactory<T>.Instance read(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if(!obj.has("type")) {
                throw new JsonSyntaxException(conditionTypeName + " json requires \"type\" identifier.");
            }
            String typeIdentifier = GsonHelper.getAsString(obj, "type");
            ResourceLocation type = ResourceLocation.tryParse(typeIdentifier);
            Optional<ConditionFactory<T>> optionalCondition = conditionRegistry.getOptional(type);
            if(!optionalCondition.isPresent()) {
                if(NamespaceAlias.hasAlias(type)) {
                    optionalCondition = conditionRegistry.getOptional(NamespaceAlias.resolveAlias(type));
                }
                if(!optionalCondition.isPresent()) {
                    throw new JsonSyntaxException(conditionTypeName + " json type \"" + type.toString() + "\" is not defined.");
                }
            }
            return optionalCondition.get().read(obj);
        }
        throw new JsonSyntaxException(conditionTypeName + " has to be a JsonObject!");
    }
}
