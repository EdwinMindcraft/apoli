package io.github.apace100.apoli.global;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalPowerSetUtil {

    public static ResourceLocation POWER_SOURCE = Apoli.identifier("global");

    public static List<GlobalPowerSet> getApplicableSets(EntityType<?> type) {
        List<GlobalPowerSet> result = new LinkedList<>();
        Iterator<GlobalPowerSet> sets = CalioAPI.getDynamicRegistries().get(ApoliDynamicRegistries.GLOBAL_POWER_SET).stream().iterator();
        while(sets.hasNext()) {
            GlobalPowerSet gps = sets.next();
            if(gps.doesApply(type)) {
                result.add(gps);
            }
        }
        result.sort(GlobalPowerSet::compareTo);
        return result;
    }

    public static Set<ResourceLocation> getPowerTypeIds(List<GlobalPowerSet> powerSets) {
        return powerSets.stream()
                .flatMap(gps -> gps.powers().stream())
                .map(ResourceKey::location)
                .collect(Collectors.toSet());
    }

    public static void applyGlobalPowers(Entity entity) {
        IPowerContainer pc = ApoliAPI.getPowerContainer(entity);
        if(pc == null) {
            return;
        }
        List<GlobalPowerSet> sets = getApplicableSets(entity.getType());
        Set<ResourceLocation> ids = getPowerTypeIds(sets);
        boolean change = removeExcessPowers(pc, ids);
        for(GlobalPowerSet powerSet : sets) {
            change |= addMissingPowers(pc, powerSet);
        }
        if(change) {
            pc.sync();
        }
    }

    private static boolean removeExcessPowers(IPowerContainer phc, Set<ResourceLocation> expected) {
        List<ResourceKey<ConfiguredPower<?, ?>>> powers = phc.getPowersFromSource(POWER_SOURCE);
        List<ResourceKey<ConfiguredPower<?, ?>>> toRemove = new LinkedList<>();
        for(ResourceKey<ConfiguredPower<?, ?>> pt : powers) {
            ResourceLocation id = pt.location();
            if(!expected.contains(id)) {
                toRemove.add(pt);
            }
        }
        for(ResourceKey<ConfiguredPower<?, ?>> pt : toRemove) {
            phc.removePower(pt, POWER_SOURCE);
        }
        return toRemove.size() > 0;
    }

    private static boolean addMissingPowers(IPowerContainer phc, GlobalPowerSet powerSet) {
        boolean added = false;
        for(ResourceKey<ConfiguredPower<?, ?>> pt : powerSet.powers()) {
            if(!phc.hasPower(pt, POWER_SOURCE)) {
                phc.addPower(pt, POWER_SOURCE);
                added = true;
            }
        }
        return added;
    }
}