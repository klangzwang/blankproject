package geni.witherdata.generator;

import java.util.concurrent.CompletableFuture;

import geni.withercore.init.WUTEntities;
import geni.witherutils.WitherUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class WitherUtilsEntityTags extends EntityTypeTagsProvider {

    public WitherUtilsEntityTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(generator.getPackOutput(), lookupProvider, WitherUtils.MODID, existingFileHelper);
    }
    
	@Override
    protected void addTags(Provider p_256380_)
    {
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(WUTEntities.DRONE_DELIVER.get());
    }
    
    @Override
    public String getName()
    {
        return "WitherUtils EntityTypeTags";
    }
}