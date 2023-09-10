package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.util.ArgumentWrapper;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;

public record SelectorConfiguration(ArgumentWrapper<EntitySelector> selector,
                                    @MustBeBound Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction,
                                    Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition) implements IDynamicFeatureConfiguration {
    public static final Codec<SelectorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ApoliDataTypes.ENTITIES_SELECTOR.fieldOf("selector").forGetter(SelectorConfiguration::selector),
            ConfiguredBiEntityAction.required("bientity_action").forGetter(SelectorConfiguration::biEntityAction),
            ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(SelectorConfiguration::biEntityCondition)
    ).apply(instance, SelectorConfiguration::new));
}
