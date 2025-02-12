package io.github.apace100.apoli.power;

public class PlayerAbilityPower {
/*
    private final PlayerAbility ability;
    private final AbilitySource source;

    public PlayerAbilityPower(PowerType<?> type, LivingEntity entity, PlayerAbility playerAbility) {
        super(type, entity);
        this.ability = playerAbility;
        if(entity instanceof PlayerEntity) {
            this.setTicking(true);
        }
        source = Pal.getAbilitySource(type.getIdentifier());
    }

    @Override
    public void tick() {
        if(!entity.world.isClient) {
            boolean isActive = isActive();
            boolean hasAbility = hasAbility();
            if(isActive && !hasAbility) {
                grantAbility();
            } else if(!isActive && hasAbility) {
                revokeAbility();
            }
        }
    }

    @Override
    public void onGained() {
        if(!entity.world.isClient &&
            entity instanceof PlayerEntity &&
            isActive() &&
            !hasAbility()) {
            grantAbility();
        }
    }

    @Override
    public void onAdded() {
        if(!entity.world.isClient &&
            entity instanceof PlayerEntity player &&
            Apoli.LEGACY_POWER_SOURCE.grants(player, ability)) {
            Apoli.LEGACY_POWER_SOURCE.revokeFrom(player, ability);
        }
    }

    @Override
    public void onLost() {
        if(!entity.world.isClient &&
            entity instanceof PlayerEntity &&
            hasAbility()) {
            revokeAbility();
        }
    }

    public boolean hasAbility() {
        return source.grants((PlayerEntity)entity, ability);
    }

    public void grantAbility() {
        source.grantTo((PlayerEntity)entity, ability);
    }

    public void revokeAbility() {
        source.revokeFrom((PlayerEntity)entity, ability);
    }

    public static PowerFactory createAbilityFactory(Identifier identifier, PlayerAbility ability) {
        return new PowerFactory<>(identifier,
            new SerializableData(),
            data ->
                (type, player) -> new PlayerAbilityPower(type, player, ability))
            .allowCondition();
    }*/
}
