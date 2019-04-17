package org.dimdev.jeid.mixin.modsupport.worldedit;

import com.sk89q.worldedit.blocks.BaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(BaseBlock.class)
public class MixinBaseBlock {
    @Shadow private short id;

    @Overwrite(remap = false)
    protected final void internalSetId(int id) {
        if (id > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Can't have a block ID above 32767 (" + id + " given)");
        } else if (id < 0) {
            throw new IllegalArgumentException("Can't have a block ID below 0");
        } else {
            this.id = (short)id;
        }
    }
}
