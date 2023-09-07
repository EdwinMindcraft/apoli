package io.github.apace100.apoli.global;

public class GlobalPowerSetLoader /* extends JsonDataLoader implements IdentifiableResourceReloadListener*/ {
/*
    public static final Set<Identifier> DEPENDENCIES = Set.of(Apoli.identifier("powers"));

    public static List<GlobalPowerSet> ALL = new LinkedList<>();

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public GlobalPowerSetLoader() {
        super(GSON, "global_powers");

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            GlobalPowerSetUtil.applyGlobalPowers(entity);
        });
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ALL.clear();
        prepared.forEach((id, json) -> {
            if(json.isJsonObject()) {
                SerializableData.Instance data = GlobalPowerSet.DATA.read(json.getAsJsonObject());
                GlobalPowerSet gps = GlobalPowerSet.FACTORY.fromData(data);
                ALL.add(gps);
            }
        });
        Apoli.LOGGER.info("Loaded " + ALL.size() + " global power sets.");
    }

    @Override
    public Identifier getFabricId() {
        return Apoli.identifier("global_powers");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return DEPENDENCIES;
    }
 */
}