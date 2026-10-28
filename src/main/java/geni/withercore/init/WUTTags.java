package geni.withercore.init;

import geni.witherutils.WitherUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class WUTTags {

    public static class Items {
        public static final TagKey<Item> COLLECTOR_BLACKLIST = TagKey.create(Registries.ITEM, WitherUtils.loc("collector_blacklist"));
    }
}
