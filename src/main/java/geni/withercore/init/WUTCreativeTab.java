package geni.withercore.init;

import geni.withercore.item.WitherItem;
import geni.withercore.registration.impl.BlockDeferredRegister;
import geni.withercore.registration.impl.BlockRegistryObject;
import geni.withercore.registration.impl.ItemDeferredRegister;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.item.BatteryItem;
import geni.witherutils.common.world.item.device.DeviceProgItem;
import geni.witherutils.common.world.level.block.CutterBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WUTCreativeTab {

	public static final DeferredRegister<CreativeModeTab> TAB_TYPES = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WitherUtils.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_BLOCKS =
			TAB_TYPES.register("tabwu_blocks", () -> {
			CreativeModeTab tab = CreativeModeTab.builder()
			.backgroundTexture(CreativeModeTab.createTextureLocation("wither_blue"))
			.icon(() -> new ItemStack(WUTItems.ICON.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.witherutils.blocks"))
			.displayItems((features, output) -> {

				output.accept(new ItemStack(WUTBlocks.ANVIL.get()));
                output.accept(new ItemStack(WUTBlocks.SOLARCASE.get()));
                output.accept(new ItemStack(WUTBlocks.SOLARBASIC.get()));
                output.accept(new ItemStack(WUTBlocks.SOLARADV.get()));
                output.accept(new ItemStack(WUTBlocks.SOLARULTRA.get()));

				output.accept(new ItemStack(WUTBlocks.RACK_CASE.get()));
				output.accept(new ItemStack(WUTBlocks.RACK_TERMINAL.get()));
				output.accept(new ItemStack(WUTBlocks.RACKFLUID_CONTROLLER.get()));
				output.accept(new ItemStack(WUTBlocks.RACKITEM_CONTROLLER.get()));
				output.accept(new ItemStack(WUTBlocks.RACKENERGY_CONTROLLER.get()));

				output.accept(new ItemStack(WUTBlocks.FLOORSENSOR.get()));
				output.accept(new ItemStack(WUTBlocks.WALL_SENSOR.get()));

				output.accept(new ItemStack(WUTBlocks.GREENHOUSE.get()));
				/*output.accept(new ItemStack(WUTBlocks.RESERVOIR.get()));
				output.accept(new ItemStack(WUTBlocks.SUPPLY.get()));*/
				/*output.accept(new ItemStack(WUTBlocks.COLLECTOR.get()));*/

				output.accept(new ItemStack(WUTBlocks.STEEL_BEAM.get()));
				output.accept(new ItemStack(WUTBlocks.STEEL_POLE.get()));
				output.accept(new ItemStack(WUTBlocks.STEEL_RAILING.get()));
				output.accept(new ItemStack(WUTBlocks.STEEL_CATWALK.get()));
				/*output.accept(new ItemStack(WUTBlocks.METAL_DOOR.get()));
				output.accept(new ItemStack(WUTBlocks.STRIPED_DOOR.get()));
				output.accept(new ItemStack(WUTBlocks.STEEL_DOOR.get()));
				output.accept(new ItemStack(WUTBlocks.LIRON_DOOR.get()));
				output.accept(new ItemStack(WUTBlocks.CREEP_DOOR.get()));
				output.accept(new ItemStack(WUTBlocks.CASED_DOOR.get()));*/


				output.accept(new ItemStack(WUTBlocks.ANGEL.get()));

				output.accept(new ItemStack(WUTBlocks.BRICKSDARK.get()));
				output.accept(new ItemStack(WUTBlocks.BRICKSLAVA.get()));

				for(var blocks : BuiltInRegistries.BLOCK.entrySet())
				{
					var block = blocks.getValue();
					if(block instanceof CutterBlock cutterBlock)
					{
						output.accept(new ItemStack(cutterBlock));
					}
				}

				output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEBLACK.get()));
				output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEGRAY.get()));
				output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEWHITE.get()));

				/*output.accept(new ItemStack(WUTBlocks.SPLINE_NODE.get()));*/

				/*output.accept(new ItemStack(WUTBlocks.LIGHT.get()));*/

				/*output.accept(new ItemStack(WUTBlocks.DEVICE.get()));
				output.accept(new ItemStack(WUTBlocks.WITHEREARTH.get()));*/

				/*output.accept(new ItemStack(WUTBlocks.VANISH_BLOCK.get()));*/

				output.accept(new ItemStack(WUTBlocks.DRONEPLATFORM.get()));
				output.accept(new ItemStack(WUTBlocks.DRONEPLATFORM_IO.get()));

				output.accept(new ItemStack(WUTBlocks.CREATIVE_GENERATOR.get()));
			})
			.build();
		return tab;
	});
	
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_ITEMS =
			TAB_TYPES.register("tabwu_items", () -> {
			CreativeModeTab tab = CreativeModeTab.builder()
			.backgroundTexture(CreativeModeTab.createTextureLocation("wither_blue"))
			.icon(() -> new ItemStack(WUTItems.ICON.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.witherutils.items"))
			.displayItems((features, output) -> {

				/*output.accept(new ItemStack(WUTItems.HOTINGOT.get()));*/

				output.accept(new ItemStack(WUTItems.WITHERSTEEL_ROD.get()));
				output.accept(new ItemStack(WUTItems.HAMMER.get()));
				output.accept(new ItemStack(WUTItems.CUTTER.get()));
				output.accept(new ItemStack(WUTItems.WRENCH.get()));

				output.accept(new ItemStack(WUTItems.WITHERSTEEL_CASE.get()));
				output.accept(new ItemStack(WUTItems.WITHERSTEEL_PLATE.get()));
				output.accept(new ItemStack(WUTItems.WITHERSTEEL_GEAR.get()));
				output.accept(new ItemStack(WUTBlocks.WITHERSTEEL_BLOCK.get()));
				output.accept(new ItemStack(WUTItems.WITHERSTEEL_INGOT.get()));
				output.accept(new ItemStack(WUTItems.WITHERSTEEL_NUGGET.get()));

				output.accept(new ItemStack(WUTItems.SHIELDBASIC.get()));
				output.accept(new ItemStack(WUTItems.SHIELDHERO.get()));
				output.accept(new ItemStack(WUTItems.SHIELDROTTEN.get()));
				output.accept(new ItemStack(WUTItems.SHIELDENERGY.get()));

                /*output.accept(new ItemStack(WUTItems.SOULBANK_CASE.get()));
                output.accept(new ItemStack(WUTItems.SOULBANK_BASIC.get()));
                output.accept(new ItemStack(WUTItems.SOULBANK_ADVANCED.get()));
                output.accept(new ItemStack(WUTItems.SOULBANK_ULTRA.get()));*/

				/*output.accept(new ItemStack(WUTItems.CARD.get()));*/

				/*output.accept(new ItemStack(WUTItems.DRONE_DELIVER.get()));
				output.accept(new ItemStack(WUTItems.GPS_TOOL.get()));*/

				/*output.accept(BatteryItem.createEmptyStack());
				output.accept(BatteryItem.createChargedStack());*/

				/*output.accept(new ItemStack(WUTItems.EGG_CURSEDCREEPER.get()));
				output.accept(new ItemStack(WUTItems.EGG_CURSEDDRYHEAD.get()));
				output.accept(new ItemStack(WUTItems.EGG_CURSEDSKELETON.get()));
				output.accept(new ItemStack(WUTItems.EGG_CURSEDSPIDER.get()));
				output.accept(new ItemStack(WUTItems.EGG_CURSEDZOMBIE.get()));*/

				/*output.accept(new ItemStack(WUTItems.FEATHER.get()));*/

				/*output.accept(new ItemStack(WUTItems.BLUELIMBO_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.COLDSLUSH_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.EXPERIENCE_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.FERTILIZER_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.PORTIUM_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.REDRESIN_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.SOULFUL_BUCKET.get()));
				output.accept(new ItemStack(WUTItems.WITHERWATER_BUCKET.get()));*/

				/*for(var items : BuiltInRegistries.ITEM.entrySet())
				{
					var item = items.getValue();
					if(item instanceof DeviceProgItem progItem)
					{
						output.accept(new ItemStack(progItem));
					}
				}*/

			})
			.build();
		return tab;
	});
	
    public static void init(IEventBus bus)
    {
        TAB_TYPES.register(bus);
    }

	public static void addToDisplay(CreativeModeTab.Output output, Collection<? extends Holder<Item>> items) {
		for (Holder<Item> itemProvider : items) {
			addToDisplay(output, itemProvider);
		}
	}

	public static void addToDisplay(CreativeModeTab.Output output, BlockRegistryObject<?, ?>... blocks) {
		addToDisplay(output, Arrays.stream(blocks).map(BlockRegistryObject::getItemHolder).toArray(Holder[]::new));
	}

	@SafeVarargs
	public static void addToDisplay(CreativeModeTab.Output output, Holder<Item>... items) {
		CreativeModeTab.TabVisibility visibility;
		if (output instanceof BuildCreativeModeTabContentsEvent) {
			visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
		} else {
			visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
		}
		for (Holder<Item> item : items) {
			Item itemLike = item.value();
			if (itemLike instanceof ICustomCreativeTabContents contents) {
				if (contents.addDefault()) {
					output.accept(itemLike, visibility);
				}
				contents.addItems(item, stack -> output.accept(stack, visibility));
			} else {
				output.accept(itemLike, visibility);
			}
		}
	}

	public static void addToDisplay(ItemDeferredRegister register, CreativeModeTab.Output output) {
		addToDisplay(output, register.getEntries());
	}

	public static void addToDisplay(BlockDeferredRegister register, CreativeModeTab.Output output) {
		addToDisplay(output, register.getSecondaryEntries());
	}
	public interface ICustomCreativeTabContents {

		void addItems(Holder<Item> item, Consumer<ItemStack> addToTab);

		default boolean addDefault() {
			return true;
		}
	}
}
