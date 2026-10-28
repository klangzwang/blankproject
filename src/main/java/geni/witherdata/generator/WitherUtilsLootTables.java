package geni.witherdata.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTEntities;
import geni.withercore.init.WUTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class WitherUtilsLootTables extends LootTableProvider {

    public WitherUtilsLootTables(DataGenerator dataGeneratorIn, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(dataGeneratorIn.getPackOutput(), Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTableWUT::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(EntityLootTableWUT::new, LootContextParamSets.ENTITY)
        ), lookupProvider);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector)
    {
    }

    private static class EntityLootTableWUT extends EntityLootSubProvider {

        public EntityLootTableWUT(HolderLookup.Provider registries)
        {
            super(FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        public void generate() {
            this.add(WUTEntities.DRONE_DELIVER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(WUTItems.DRONE_DELIVER.get()))));
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return Stream.of(WUTEntities.DRONE_DELIVER.get());
        }
    }

    private static class BlockLootTableWUT extends BlockLootSubProvider
    {
        public BlockLootTableWUT(HolderLookup.Provider provider)
        {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
        }

        @Override
        protected void generate()
        {
            add(WUTBlocks.SOULFIRE.get(), noDrop());
            add(WUTBlocks.WITHEREARTH.get(), createSingleItemTable(Items.DIRT));

            for (var holder: WUTBlocks.BLOCKS.getPrimaryEntries())
            {
                Block b = holder.get();
                if (b.asItem() != Items.AIR && !(b instanceof LiquidBlock) && b != WUTBlocks.SOULFIRE.get() && b != WUTBlocks.WITHEREARTH.get())
                {
                    dropSelf(b);
                }
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            List<Block> l = new ArrayList<>();
            for (var holder: WUTBlocks.BLOCKS.getPrimaryEntries())
            {
                if (BuiltInRegistries.ITEM.containsKey(holder.getId()))
                {
                    l.add(holder.get());
                }
            }
            return l;
        }
    }
}
