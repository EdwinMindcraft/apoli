package io.github.edwinmindcraft.apoli.common.power;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class PreventFeatureRenderPower extends PowerFactory<ListConfiguration<String>> {
	@OnlyIn(Dist.CLIENT)
	public static boolean doesPrevent(Entity entity, RenderLayer<?, ?> layer) {
		Optional<ClassDataRegistry<RenderLayer<?, ?>>> optionalCdr = ClassDataRegistry.get(ClassUtil.castClass(RenderLayer.class));
		if (optionalCdr.isPresent()) {
			ClassDataRegistry<? extends RenderLayer<?, ?>> cdr = optionalCdr.get();
			return PowerContainer.getPowers(entity, ApoliPowers.PREVENT_FEATURE_RENDER.get()).stream()
					.flatMap(power -> power.value().getConfiguration().entries().stream().flatMap(x -> cdr.mapStringToClass(x).stream()))
					.anyMatch(x -> x.isInstance(layer));
		}
		return false;
	}

	public PreventFeatureRenderPower() {
		super(ListConfiguration.codec(Codec.STRING, "feature", "features"));
	}
}
