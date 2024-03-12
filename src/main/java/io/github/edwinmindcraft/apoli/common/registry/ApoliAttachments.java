package io.github.edwinmindcraft.apoli.common.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class ApoliAttachments {
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<@NotNull Float>> DAMAGE_CACHE = ApoliRegisters.ATTACHMENT_TYPES.register("damage_cache", () -> AttachmentType.builder(() -> Float.NaN).build());

	public static void bootstrap() {}
}
