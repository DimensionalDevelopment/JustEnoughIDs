package org.dimdev.jeid.mixin.modsupport.bewitchment;

import com.bewitchment.common.world.BiomeChangingUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(BiomeChangingUtils.class)
public class MixinUtils {
	@Overwrite
	public static void setBiome(World world, Biome biome, BlockPos pos) {
		if (biome == null) return;

		INewChunk newChunk = (INewChunk) world.getChunk(pos);
		int[] array = newChunk.getIntBiomeArray();
		array[(pos.getX() & 15) << 4 | pos.getZ() & 15] = Biome.getIdForBiome(biome) & 255;
		newChunk.setIntBiomeArray(array);

		if (!world.isRemote) {
			NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), world.getHeight(pos).getY(), pos.getZ(), 32.0D);
			MessageManager.CHANNEL.sendToAllAround(new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome)), point);
		}
	}
}
