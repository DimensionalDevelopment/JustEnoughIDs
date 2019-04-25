package org.dimdev.jeid;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(-7500)
@IFMLLoadingPlugin.Name("JustEnoughIDs Extension Plugin")
//@IFMLLoadingPlugin.TransformerExclusions("org.dimdev.jeid.")
public class JEIDLoadingPlugin implements IFMLLoadingPlugin {

    public JEIDLoadingPlugin() {
        MixinBootstrap.init();
        Utils.LOGGER.info("Initializing JustEnoughIDs core mixins");
        Mixins.addConfiguration("mixins.jeid.core.json");
        Utils.LOGGER.info("Initializing JustEnoughIDs initialization mixins");
        Mixins.addConfiguration("mixins.jeid.init.json");
    }

    @Override public String[] getASMTransformerClass() { Obf.loadData(); return new String[]{ "org.dimdev.jeid.JEIDTransformer" }; }
    @Override public String getModContainerClass() { return null; }
    @Nullable @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }
}
