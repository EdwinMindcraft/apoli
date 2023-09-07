package io.github.apace100.apoli.global;

public class GlobalPowerSet /*implements Comparable<GlobalPowerSet>, DataObject<GlobalPowerSet>*/ {
/*
    private final int order;
    private final TagLike<EntityType<?>> entityTypes;

    public List<PowerType<?>> getPowerTypes() {
        return powerTypes;
    }

    private final List<PowerType<?>> powerTypes;

    public GlobalPowerSet(int order, TagLike<EntityType<?>> entityTypes, List<PowerType<?>> powerTypes) {
        this.order = order;
        this.entityTypes = entityTypes;
        this.powerTypes = powerTypes;
        Apoli.LOGGER.info("Created global power set");
    }

    public boolean doesApply(EntityType<?> entityType) {
        return entityTypes == null || entityTypes.contains(entityType);
    }

    public boolean doesApply(Entity entity) {
        return doesApply(entity.getType());
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(@NotNull GlobalPowerSet o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public DataObjectFactory<GlobalPowerSet> getFactory() {
        return FACTORY;
    }

    public static final SerializableData DATA = new SerializableData()
            .add("entity_types", TagLike.dataType(Registries.ENTITY_TYPE), null)
            .add("powers", SerializableDataType.list(ApoliDataTypes.POWER_TYPE))
            .add("order", SerializableDataTypes.INT, 0);

    public static final DataObjectFactory<GlobalPowerSet> FACTORY = new Factory();

    private static class Factory implements DataObjectFactory<GlobalPowerSet> {

        @Override
        public SerializableData getData() {
            return DATA;
        }

        @Override
        public GlobalPowerSet fromData(SerializableData.Instance instance) {
            return new GlobalPowerSet(instance.getInt("order"), instance.get("entity_types"), instance.get("powers"));
        }

        @Override
        public SerializableData.Instance toData(GlobalPowerSet globalPowerSet) {
            SerializableData.Instance inst = DATA.new Instance();
            inst.set("order", globalPowerSet.order);
            inst.set("entity_types", globalPowerSet.entityTypes);
            inst.set("powers", globalPowerSet.powerTypes);
            return inst;
        }
    }
 */
}