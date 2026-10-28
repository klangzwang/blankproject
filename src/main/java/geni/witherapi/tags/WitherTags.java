package geni.witherapi.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class WitherTags {

    public static final String MODID = "witherutils";

    private WitherTags() {}

    public static final class Items {

        public static final TagKey<Item> WRENCHES = TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "tools/wrench")
        );

        public static final TagKey<Item> WITHER_WRENCHES = TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MODID, "wrenches")
        );

        private Items() {}
    }
}
