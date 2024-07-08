package io.github.apace100.apoli.util;

import io.github.apace100.apoli.access.ModifiedPoseHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public enum ArmPoseReference {

    EMPTY,
    ITEM,
    BLOCK,
    BOW_AND_ARROW,
    THROW_SPEAR,
    CROSSBOW_CHARGE,
    CROSSBOW_HOLD,
    SPYGLASS,
    TOOT_HORN,
    BRUSH;

    public static Optional<HumanoidModel.ArmPose> getArmPose(Entity entity) {

        if (!(entity instanceof ModifiedPoseHolder poseHolder) || poseHolder.apoli$getModifiedArmPose() == null) {
            return Optional.empty();
        }

        return Optional.of(switch (poseHolder.apoli$getModifiedArmPose()) {
            case EMPTY ->
                    HumanoidModel.ArmPose.EMPTY;
            case ITEM ->
                    HumanoidModel.ArmPose.ITEM;
            case BLOCK ->
                    HumanoidModel.ArmPose.BLOCK;
            case BRUSH ->
                    HumanoidModel.ArmPose.BRUSH;
            case SPYGLASS ->
                    HumanoidModel.ArmPose.SPYGLASS;
            case TOOT_HORN ->
                    HumanoidModel.ArmPose.TOOT_HORN;
            case THROW_SPEAR ->
                    HumanoidModel.ArmPose.THROW_SPEAR;
            case BOW_AND_ARROW ->
                    HumanoidModel.ArmPose.BOW_AND_ARROW;
            case CROSSBOW_HOLD ->
                    HumanoidModel.ArmPose.CROSSBOW_HOLD;
            case CROSSBOW_CHARGE ->
                    HumanoidModel.ArmPose.CROSSBOW_CHARGE;
        });

    }

}
