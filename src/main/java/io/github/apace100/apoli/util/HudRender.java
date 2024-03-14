package io.github.apace100.apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record HudRender(boolean shouldRender, int barIndex, ResourceLocation spriteLocation,
						Holder<ConfiguredEntityCondition<?, ?>> condition,
						boolean inverted) implements IDynamicFeatureConfiguration {

	private static final ResourceLocation DEFAULT_SPRITE = new ResourceLocation("origins", "textures/gui/resource_bar.png");

	public static final MapCodec<HudRender> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "should_render", true).forGetter(HudRender::shouldRender),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "bar_index", 0).forGetter(HudRender::barIndex),
			ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "sprite_location", DEFAULT_SPRITE).forGetter(HudRender::spriteLocation),
			ConfiguredEntityCondition.optional("condition").forGetter(HudRender::condition),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "inverted", false).forGetter(HudRender::inverted)
	).apply(instance, HudRender::new));

	public static final Codec<HudRender> CODEC = MAP_CODEC.codec();

	public static final HudRender DONT_RENDER = new HudRender(false, 0, DEFAULT_SPRITE, null, false);

	public ResourceLocation getSpriteLocation() {
		return this.spriteLocation();
	}

	public int getBarIndex() {
		return this.barIndex();
	}

	public boolean isInverted() {
		return this.inverted();
	}

	public boolean shouldRender(Entity entity) {
		return this.shouldRender() && ConfiguredEntityCondition.check(this.condition(), entity);
	}

	//Kept to prevent binary breaking changes.
	public boolean shouldRender(Player player) {
		return this.shouldRender((Entity) player);
	}

	public Holder<ConfiguredEntityCondition<?, ?>> getCondition() {
		return this.condition();
	}
}
