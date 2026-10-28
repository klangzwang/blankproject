package geni.witherapi.providers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@MethodsReturnNonnullByDefault
public interface IBaseProvider {

    ResourceLocation getRegistryName();
    
    default String getName()
    {
        return getRegistryName().getPath();
    }
}