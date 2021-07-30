package io.github.apace100.apoli.power.factory.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.apace100.apoli.util.NamespaceAlias;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ActionType<T> {

    private final String actionTypeName;
    private final Registry<ActionFactory<T>> actionFactoryRegistry;

    public ActionType(String actionTypeName, Registry<ActionFactory<T>> actionFactoryRegistry) {
        this.actionTypeName = actionTypeName;
        this.actionFactoryRegistry = actionFactoryRegistry;
    }

    public void write(FriendlyByteBuf buf, ActionFactory.Instance actionInstance) {
        actionInstance.write(buf);
    }

    public ActionFactory<T>.Instance read(FriendlyByteBuf buf) {
        ResourceLocation type = buf.readResourceLocation();
        ActionFactory<T> actionFactory = actionFactoryRegistry.get(type);
        if(actionFactory == null) {
            throw new JsonSyntaxException(actionTypeName + " \"" + type + "\" was not registered.");
        }
        return actionFactory.read(buf);
    }

    public ActionFactory<T>.Instance read(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if(!obj.has("type")) {
                throw new JsonSyntaxException(actionTypeName + " json requires \"type\" identifier.");
            }
            String typeIdentifier = GsonHelper.getAsString(obj, "type");
            ResourceLocation type = ResourceLocation.tryParse(typeIdentifier);
            Optional<ActionFactory<T>> optionalAction = actionFactoryRegistry.getOptional(type);
            if(!optionalAction.isPresent()) {
                if(NamespaceAlias.hasAlias(type)) {
                    optionalAction = actionFactoryRegistry.getOptional(NamespaceAlias.resolveAlias(type));
                }
                if(!optionalAction.isPresent()) {
                    throw new JsonSyntaxException(actionTypeName + " json type \"" + type.toString() + "\" is not defined.");
                }
            }
            return optionalAction.get().read(obj);
        }
        throw new JsonSyntaxException(actionTypeName + " has to be a JsonObject!");
    }
}
