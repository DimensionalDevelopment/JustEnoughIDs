package org.dimdev.jeid.mixin.modsupport.twilightforest;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TwilightMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {}

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        for (ModContainer mod : Loader.instance().getModList()) {
            if (mod.getModId().equals("twilightforest")) {
                if (s1.equals("org.dimdev.jeid.mixin.modsupport.twilightforest.MixinBlockTFMagicLogSpecial_Old")) {
                    if (Integer.parseInt(mod.getVersion().split("[.]")[2]) < 689) {
                        JEID.LOGGER.info("Older TwilightForest version (<689). Using mixin: " + s1);
                        return true;
                    } else {
                        return false;
                    }
                } else if (s1.equals("org.dimdev.jeid.mixin.modsupport.twilightforest.MixinBlockTFMagicLogSpecial")) {
                    if (Integer.parseInt(mod.getVersion().split("[.]")[2]) > 689) {
                        JEID.LOGGER.info("Newer TwilightForest version (>689). Using mixin: " + s1);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
