package geni.witherdata.generator;

import geni.witherutils.WitherUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WitherUtilsEntityTags extends EntityTypeTagsProvider {

    public WitherUtilsEntityTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(generator.getPackOutput(), lookupProvider, WitherUtils.MODID, existingFileHelper);
    }
    
	@Override
    protected void addTags(Provider p_256380_)
    {
    }
    
    @Override
    public String getName()
    {
        return "WitherUtils EntityTypeTags";
    }
}