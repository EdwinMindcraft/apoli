package io.github.apace100.apoli.power.factory.action;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ActionFactory<T> {

    private final ResourceLocation identifier;
    protected SerializableData data;
    private final BiConsumer<SerializableData.Instance, T> effect;

    public ActionFactory(ResourceLocation identifier, SerializableData data, BiConsumer<SerializableData.Instance, T> effect) {
        this.identifier = identifier;
        this.effect = effect;
        this.data = data;
        this.data.add("inverted", SerializableDataTypes.BOOLEAN, false);
    }

    public class Instance implements Consumer<T> {

        private final SerializableData.Instance dataInstance;

        private Instance(SerializableData.Instance data) {
            this.dataInstance = data;
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(identifier);
            data.write(buf, dataInstance);
        }

        @Override
        public void accept(T t) {
            effect.accept(dataInstance, t);
        }
    }

    public ResourceLocation getSerializerId() {
        return identifier;
    }

    public Instance read(JsonObject json) {
        return new Instance(data.read(json));
    }

    public Instance read(FriendlyByteBuf buffer) {
        return new Instance(data.read(buffer));
    }
}
