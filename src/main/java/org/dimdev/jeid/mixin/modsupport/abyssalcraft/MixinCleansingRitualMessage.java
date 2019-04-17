package org.dimdev.jeid.mixin.modsupport.abyssalcraft;

import com.shinoow.abyssalcraft.common.network.client.CleansingRitualMessage;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(CleansingRitualMessage.class)
public class MixinCleansingRitualMessage {
    @Shadow private int x;
    @Shadow private int z;
    @Shadow private int biomeID;
    @Shadow private boolean batched;

    /**
     * @reason This exists to revert changes made by JEID
     * @author Shinoow
     */
    @Final
    @Overwrite(remap = false)
    public void process(EntityPlayer player, Side side) {
        BiomeUtil.updateBiome(player.world, new BlockPos(x, 0, z), biomeID, false);

        if(x % 14 == 0 || z % 14 == 0 || !batched)
            Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(x - 7, 0, z - 7, x + 7, 255, z + 7);
    }
}
