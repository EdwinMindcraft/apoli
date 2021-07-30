package io.github.apace100.apoli.power.factory.condition;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ConditionFactory<T> {

    private final ResourceLocation identifier;
    protected SerializableData data;
    private final BiFunction<SerializableData.Instance, T, Boolean> condition;

    public ConditionFactory(ResourceLocation identifier, SerializableData data, BiFunction<SerializableData.Instance, T, Boolean> condition) {
        this.identifier = identifier;
        this.condition = condition;
        this.data = data;
        this.data.add("inverted", SerializableDataTypes.BOOLEAN, false);
    }

    public class Instance implements Predicate<T> {

        private final SerializableData.Instance dataInstance;

        private Instance(SerializableData.Instance data) {
            this.dataInstance = data;
        }

        public final boolean test(T t) {
            boolean fulfilled = isFulfilled(t);
            if(dataInstance.getBoolean("inverted")) {
                return !fulfilled;
            } else {
                return fulfilled;
            }
        }

        public boolean isFulfilled(T t) {
            return condition.apply(dataInstance, t);
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(identifier);
            data.write(buf, dataInstance);
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
