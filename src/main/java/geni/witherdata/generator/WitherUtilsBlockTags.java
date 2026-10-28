package geni.witherdata.generator;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

import geni.withercore.init.WUTBlocks;
import geni.witherutils.WitherUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class WitherUtilsBlockTags extends BlockTagsProvider {

    public WitherUtilsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, WitherUtils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(WUTBlocks.STEEL_BEAM.get())
                .add(WUTBlocks.STEEL_POLE.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(WUTBlocks.WITHEREARTH.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(WUTBlocks.STEEL_BEAM.get())
                .add(WUTBlocks.STEEL_POLE.get());
    }

    @Override
    public String getName()
    {
        return "WitherUtils Block Tags";
    }
}
