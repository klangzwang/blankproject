package geni.witherdata.generator;

import geni.withercore.init.WUTBlocks;
import geni.witherutils.WitherUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

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

        for (var entry : WUTBlocks.CTM_CONCRETE.entrySet()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.getValue().getBlock());
        }
        for (var entry : WUTBlocks.CTM_METAL.entrySet()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.getValue().getBlock());
        }
        for (var entry : WUTBlocks.CTM_STONE.entrySet()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.getValue().getBlock());
        }
        for (var entry : WUTBlocks.CTM_WOOD.entrySet()) {
            tag(BlockTags.MINEABLE_WITH_AXE).add(entry.getValue().getBlock());
        }
    }

    @Override
    public String getName()
    {
        return "WitherUtils Block Tags";
    }
}
