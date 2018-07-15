package org.dimdev.jeid.mixin.core.client;

import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldSummary.class)
public class MixinWorldSummary {
    @Shadow @Final private int versionId;

    @Overwrite
    public boolean askToOpenWorld() {
        return versionId > 1343 && versionId != Integer.MAX_VALUE / 2;
    }
}
