package geni.witherdata.generator;

import geni.witherapi.tags.WitherTags;
import geni.withercore.init.WUTItems;
import geni.witherutils.WitherUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WitherUtilsItemTags extends ItemTagsProvider {
	
    public WitherUtilsItemTags(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper)
    {
        super(generatorIn.getPackOutput(), lookupProvider, blockTagsProvider, WitherUtils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider)
    {
        tag(WitherTags.Items.WITHER_WRENCHES)
                .add(WUTItems.WRENCH.get());

        tag(WitherTags.Items.WRENCHES)
                .addTag(WitherTags.Items.WITHER_WRENCHES);
    }

    @Override
    public String getName()
    {
        return "WitherUtils Item Tags";
    }
}
