package io.github.apace100.apoli.data;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.NameMutableDamageSource;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class DamageSourceDescription {

    public static final Codec<DamageSourceDescription> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(DamageSourceDescription::getName),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "bypasses_armor", false).forGetter(o -> o.containsTag(DamageTypeTags.BYPASSES_ARMOR)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "fire", false).forGetter(o -> o.containsTag(DamageTypeTags.IS_FIRE)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "unblockable", false).forGetter(o -> o.containsTag(DamageTypeTags.BYPASSES_SHIELD)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "magic", false).forGetter(o -> o.containsTag(DamageTypeTags.WITCH_RESISTANT_TO)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "out_of_world", false).forGetter(o -> o.containsTag(DamageTypeTags.BYPASSES_INVULNERABILITY)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "projectile", false).forGetter(o -> o.containsTag(DamageTypeTags.IS_PROJECTILE)),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "explosive", false).forGetter(o -> o.containsTag(DamageTypeTags.BYPASSES_SHIELD))
    ).apply(instance, DamageSourceDescription::new));
    private static final Set<TagKey<DamageType>> TAGS = Sets.newHashSet();
    private static int TAG_COUNT = 0;

    private void registerDamageTypeTagMapping(boolean key, TagKey<DamageType> tag) {
        TAGS.add(tag);
        if (key) {
            desiredDamageTypeTags.add(tag);
        }
    }

    private final String name;
    private final Set<TagKey<DamageType>> desiredDamageTypeTags = new HashSet<>();

    private ResourceKey<DamageType> damageType;

    public DamageSourceDescription(String name, boolean bypassesArmor, boolean fire,
                                   boolean unblockable, boolean magic, boolean outOfWorld,
                                   boolean projectile, boolean explosive) {
        this.name = name;
        registerDamageTypeTagMapping(bypassesArmor, DamageTypeTags.BYPASSES_ARMOR);
        registerDamageTypeTagMapping(fire, DamageTypeTags.IS_FIRE);
        registerDamageTypeTagMapping(unblockable, DamageTypeTags.BYPASSES_SHIELD);
        registerDamageTypeTagMapping(magic, DamageTypeTags.WITCH_RESISTANT_TO);
        registerDamageTypeTagMapping(magic, DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
        registerDamageTypeTagMapping(outOfWorld, DamageTypeTags.BYPASSES_INVULNERABILITY);
        registerDamageTypeTagMapping(projectile, DamageTypeTags.IS_PROJECTILE);
        registerDamageTypeTagMapping(explosive, DamageTypeTags.IS_EXPLOSION);
        if (TAG_COUNT == 0)
            TAG_COUNT = TAGS.size();
    }

    public String getName() {
        return name;
    }

    private boolean containsTag(TagKey<DamageType> tag) {
        return desiredDamageTypeTags.contains(tag);
    }

    public Set<TagKey<DamageType>> getDamageTypeTags() {
        return Set.copyOf(desiredDamageTypeTags);
    }

    public void addDamageTypeTag(TagKey<DamageType> damageTypeTag) {
        desiredDamageTypeTags.add(damageTypeTag);
    }

    public ResourceKey<DamageType> getDamageType(DamageSources damageSources) {
        if(damageType == null) {
            findBestMatchingDamageType(damageSources);
        }
        return damageType;
    }

    public DamageSource create(DamageSources damageSources) {
        DamageSource damageSource = damageSources.source(damageType);
        overwriteDamageSourceMessageKey(damageSource);
        return damageSource;
    }

    public DamageSource create(DamageSources damageSources, Entity attacker) {
        DamageSource damageSource = damageSources.source(damageType, attacker);
        overwriteDamageSourceMessageKey(damageSource);
        return damageSource;
    }

    public DamageSource create(DamageSources damageSources, Entity source, Entity attacker) {
        DamageSource damageSource = damageSources.source(damageType, source, attacker);
        overwriteDamageSourceMessageKey(damageSource);
        return damageSource;
    }

    private void overwriteDamageSourceMessageKey(DamageSource source) {
        ((NameMutableDamageSource)source).setName(name);
    }

    private void findBestMatchingDamageType(DamageSources damageSources) {
        Optional<? extends Holder<DamageType>> bestMatchingDamageType = damageSources.damageTypes.holders()
                .max(Comparator.comparingInt(this::getTagMatches));
        if(bestMatchingDamageType.isPresent()) {
            Holder<DamageType> bestMatch = bestMatchingDamageType.get();
            int bestMatchTagCount = getTagMatches(bestMatch);
            if(bestMatchTagCount < TAG_COUNT) {
                Apoli.LOGGER.warn("Could not find a perfect damage type for legacy damage source field, best match: {} out of {} tags with damage type \"{}\". Consider creating your own custom damage type.",
                        bestMatchTagCount, TAG_COUNT, bestMatch.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse("<unknown>"));
            }
            damageType = bestMatch.unwrapKey().orElseThrow();
        } else {
            throw new NoSuchElementException("Damage type registry was empty or not loaded yet");
        }
    }

    private int getTagMatches(Holder<DamageType> damageType) {
        int count = 0;
        for(TagKey<DamageType> tag : TAGS) {
            if(damageType.is(tag) == desiredDamageTypeTags.contains(tag)) {
                count++;
            }
        }
        return count;
    }

    /*
    public static DamageSourceDescription fromData(SerializableData.Instance dataInstance) {
        DamageSourceDescription damageSourceDescription = new DamageSourceDescription(dataInstance.getString("name"));
        for(String jsonKey : STRING_TO_TAGS.keySet()) {
            if(dataInstance.getBoolean(jsonKey)) {
                STRING_TO_TAGS.get(jsonKey).forEach(damageSourceDescription::addDamageTypeTag);
            }
        }
        return damageSourceDescription;
    }

    public static SerializableData.Instance toData(SerializableData data, DamageSourceDescription damageSourceDescription) {
        SerializableData.Instance instance = data.new Instance();
        instance.set("name", damageSourceDescription.getName());
        Set<TagKey<DamageType>> tags = damageSourceDescription.getDamageTypeTags();
        for(Map.Entry<TagKey<DamageType>, String> damageTagPair : TAG_TO_STRING.entrySet()) {
            TagKey<DamageType> tag = damageTagPair.getKey();
            String jsonKey = damageTagPair.getValue();
            boolean isTrueAlready = instance.isPresent(jsonKey) && instance.getBoolean(jsonKey);
            instance.set(jsonKey, isTrueAlready || tags.contains(tag));
        }
        return instance;
    }
     */
}