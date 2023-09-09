package io.github.apace100.apoli.power;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Prioritized<C extends IDynamicFeatureConfiguration, T extends PowerFactory<C> & Prioritized<C, T>> {

    int getPriority(C configuration);

    class CallInstance<C extends IDynamicFeatureConfiguration, T extends PowerFactory<C> & Prioritized<C, T>> {

        private final HashMap<Integer, List<Holder<ConfiguredPower<C, T>>>> buckets = new HashMap<>();
        private int minPriority = Integer.MAX_VALUE;
        private int maxPriority = Integer.MIN_VALUE;

        public <U extends Holder<ConfiguredPower<C, T>>> void add(LivingEntity entity, T factory) {
            add(entity, factory, null);
        }

        public <U extends Holder<ConfiguredPower<C, T>>> void add(Entity entity, T factory, Predicate<U> filter) {
            Stream<U> stream = (Stream<U>)(Object)ApoliAPI.getPowerContainer(entity).getPowers(factory).stream();
            if(filter != null) {
                stream = stream.filter(u -> u.isBound() && filter.test(u));
            }
            stream.forEach(this::add);
        }

        public int getMinPriority() {
            return minPriority;
        }

        public int getMaxPriority() {
            return maxPriority;
        }

        public boolean hasPowers(int priority) {
            return buckets.containsKey(priority);
        }

        public List<Holder<ConfiguredPower<C, T>>> getPowers(int priority) {
            if(buckets.containsKey(priority)) {
                return buckets.get(priority);
            }
            return new LinkedList<>();
        }

        public void add(Holder<ConfiguredPower<C, T>> power) {
            if (!power.isBound()) {
                return;
            }
            int priority = power.value().getFactory().getPriority(power.value().getConfiguration());
            if(buckets.containsKey(priority)) {
                buckets.get(priority).add(power);
            } else {
                List<Holder<ConfiguredPower<C, T>>> list = new LinkedList<>();
                list.add(power);
                buckets.put(priority, list);
            }
            if(priority < minPriority) {
                minPriority = priority;
            }
            if(priority > maxPriority) {
                maxPriority = priority;
            }
        }
    }
}