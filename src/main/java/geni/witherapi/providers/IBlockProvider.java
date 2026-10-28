package geni.witherapi.providers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@MethodsReturnNonnullByDefault
public interface IBlockProvider extends IItemProvider {

    Block getBlock();
    
    @Override
    default ResourceLocation getRegistryName()
    {
        return BuiltInRegistries.BLOCK.getKey(getBlock());
    }
	
    @Override
    default String getTranslationKey()
    {
        return getBlock().getDescriptionId();
    }
}