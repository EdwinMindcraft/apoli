package io.github.apace100.apoli.util.modifier;

public enum ModifierOperation implements IModifierOperation {

    /*
    ADD_BASE_EARLY(Phase.BASE, 0, (values, base, current) -> base + values.stream().reduce(0.0, Double::sum)),
    MULTIPLY_BASE_ADDITIVE(Phase.BASE, 100, (values, base, current) ->
        current + (base * values.stream().reduce(0.0, Double::sum))),
    MULTIPLY_BASE_MULTIPLICATIVE(Phase.BASE, 200, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value *= (1 + v);
        }
        return value;
    }),
    ADD_BASE_LATE(Phase.BASE, 300, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value += v;
        }
        return value;
    }),
    MIN_BASE(Phase.BASE, 400, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = Math.max(v, value);
        }
        return value;
    }),
    MAX_BASE(Phase.BASE, 500, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = Math.min(v, value);
        }
        return value;
    }),
    SET_BASE(Phase.BASE, 600, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = v;
        }
        return value;
    }),
    MULTIPLY_TOTAL_ADDITIVE(Phase.TOTAL, 0, (values, base, current) ->
        current + (base * values.stream().reduce(0.0, Double::sum))),
    MULTIPLY_TOTAL_MULTIPLICATIVE(Phase.TOTAL, 100, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value *= (1 + v);
        }
        return value;
    }),
    ADD_TOTAL_LATE(Phase.TOTAL, 200, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = v;
        }
        return value;
    }),
    MIN_TOTAL(Phase.TOTAL, 300, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = Math.max(v, value);
        }
        return value;
    }),
    MAX_TOTAL(Phase.TOTAL, 400, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = Math.min(v, value);
        }
        return value;
    }),
    SET_TOTAL(Phase.TOTAL, 500, (values, base, current) -> {
        double value = current;
        for(double v : values) {
            value = v;
        }
        return value;
    });

    public static final SerializableData DATA = new SerializableData()
        .add("value", SerializableDataTypes.DOUBLE)
        .add("resource", ApoliDataTypes.POWER_TYPE, null)
        .add("modifier", Modifier.LIST_TYPE, null);

    private final Phase phase;
    private final int order;
    private final PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function;

    ModifierOperation(Phase phase, int order, PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function) {
        this.phase = phase;
        this.order = order;
        this.function = function;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /*
    @Override
    public SerializableData getData() {
        return DATA;
    }

    @Override
    public double apply(Entity entity, List<SerializableData.Instance> instances, double base, double current) {
        return function.apply(
            instances.stream()
                .map(instance -> {
                    double value = 0;
                    if(instance.isPresent("resource")) {
                        IPowerContainer.get(entity).ifPresent(component -> {
                            ConfiguredPower<?, ?> powerType = instance.get("resource");
                            if(!component.hasPower(powerType)) {
                                value = instance.get("value");
                            } else {
                                Power p = component.getPower(powerType);
                                if(p instanceof IVariableIntPower vip) {
                                    value = vip.getValue();
                                } else if(p instanceof CooldownPower cp) {
                                    value = cp.getRemainingTicks();
                                }
                            }
                        });
                    } else {
                        value = instance.get("value");
                    }
                    if(instance.isPresent("modifier")) {
                        List<Modifier> modifiers = instance.get("modifier");
                        value = ModifierUtil.applyModifiers(entity, modifiers, value);
                    }
                    return value;
                })
                .collect(Collectors.toList()),
            base, current);
    }
     */
}