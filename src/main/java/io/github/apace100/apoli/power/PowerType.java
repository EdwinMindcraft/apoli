package io.github.apace100.apoli.power;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerConfiguration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class PowerType<T extends Power> {

	private final ResourceLocation identifier;
	private final ConfiguredPower<?, ?> configuredPower;
	private final PowerFactory<T>.Instance factory;
    /*private boolean isHidden = false;

    private String nameTranslationKey;
    private String descriptionTranslationKey;*/

	public PowerType(ResourceLocation id, PowerFactory<T>.Instance factory) {
		this.identifier = id;
		this.factory = factory;
		this.configuredPower = factory.getPower();
	}

	public PowerType(ConfiguredPower<?, ?> cp) {
		this.identifier = ApoliAPI.getPowers().getKey(cp);
		this.configuredPower = cp;
		this.factory = null; //TODO Dummy Factory.
	}

	public ResourceLocation getIdentifier() {
		return this.identifier;
	}

	public PowerFactory<T>.Instance getFactory() {
		return this.factory;
	}

	public ConfiguredPower<?, ?> getConfiguredPower() {
		return this.configuredPower;
	}

	public PowerType<T> setHidden() {
		//this.isHidden = true;
		return this;
	}

	public void setTranslationKeys(String name, String description) {
		//this.nameTranslationKey = name;
		//this.descriptionTranslationKey = description;
	}

	public T create(LivingEntity entity) {
		return this.getFactory().apply(this, entity);
	}

	public boolean isHidden() {
		return this.configuredPower.getData().hidden();//isHidden;
	}

	public boolean isActive(Entity entity) {
		if (entity instanceof LivingEntity living && this.identifier != null) {
			return PowerContainer.get(living).resolve().map(x -> x.getPower(this.identifier)).map(x -> x.value().isActive(living)).orElse(false);
		}
		return false;
	}

	@SuppressWarnings({"unchecked"})
	public T get(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return PowerContainer.get(living).resolve().map(x -> x.getPower(this.identifier)).map(cp -> {
				if (cp.value().getConfiguration() instanceof FabricPowerConfiguration config)
					return (T) config.power().apply(this, living);
				return null;
			}).orElse(null);
		}
		return null;
	}

	public String getOrCreateNameTranslationKey() {
		return this.configuredPower.getData().name();
	}

	public Component getName() {
		return this.configuredPower.getData().getName();
	}

	public String getOrCreateDescriptionTranslationKey() {
		return this.configuredPower.getData().description();
	}

	public Component getDescription() {
		return this.configuredPower.getData().getDescription();
	}

	@Override
	public int hashCode() {
		return this.identifier.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof PowerType pt)) return false;
		return Objects.equals(this.identifier, pt.getIdentifier());
	}
}
