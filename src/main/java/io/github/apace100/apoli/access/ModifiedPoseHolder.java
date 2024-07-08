package io.github.apace100.apoli.access;

import io.github.apace100.apoli.util.ArmPoseReference;
import net.minecraft.world.entity.Pose;

public interface ModifiedPoseHolder {

    Pose apoli$getModifiedEntityPose();
    void apoli$setModifiedEntityPose(Pose entityPose);

    ArmPoseReference apoli$getModifiedArmPose();
    void apoli$setModifiedArmPose(ArmPoseReference armPose);

}
