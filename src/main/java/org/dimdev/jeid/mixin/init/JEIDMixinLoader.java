package org.dimdev.jeid.mixin.init;

import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class JEIDMixinLoader {
    {
        Mixins.addConfiguration("mixins.jeid.modsupport.json");
        Mixins.addConfiguration("mixins.jeid.twilightforest.json");
    }
}

/*@Mixin(Loader.class)
public class MixinLoader {
    @Shadow private List<ModContainer> mods;
    @Shadow private ModClassLoader modClassLoader;

    /**
     * @reason Load all mods now and load mod support mixin configs. This can't be done later
     * since constructing mods loads classes from them.
     /
    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/LoadController;transition(Lnet/minecraftforge/fml/common/LoaderState;Z)V", ordinal = 1), remap = false)
    private void beforeConstructingMods(List<String> injectedModContainers, CallbackInfo ci) {
        // Add all mods to class loader
        for (ModContainer mod : mods) {
            try {
                modClassLoader.addFile(mod.getSource());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        // Add and reload mixin configs
        Mixins.addConfiguration("mixins.jeid.modsupport.json");
        Mixins.addConfiguration("mixins.jeid.twilightforest.json");

        Proxy mixinProxy = (Proxy) Launch.classLoader.getTransformers().stream().filter(transformer -> transformer instanceof Proxy).findFirst().get();
        try {
            //This will very likely break on the next major mixin release.
            Class mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");

            Field transformerField = Proxy.class.getDeclaredField("transformer");
            transformerField.setAccessible(true);
            Object transformer = transformerField.get(mixinProxy);

            //Get MixinProcessor from MixinTransformer
            Field processorField = mixinTransformerClass.getDeclaredField("processor");
            processorField.setAccessible(true);
            Object processor = processorField.get(transformer);

            Method selectConfigsMethod = MixinProcessor.class.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
            selectConfigsMethod.setAccessible(true);
            selectConfigsMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());

            Method prepareConfigsMethod = MixinProcessor.class.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
            prepareConfigsMethod.setAccessible(true);
            prepareConfigsMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}*/
