package geni.withercore.init;

import geni.withercore.common.registration.impl.BlockDeferredRegister;
import geni.withercore.common.registration.impl.BlockRegistryObject;
import geni.withercore.common.registration.impl.ItemDeferredRegister;
import geni.witherutils.WitherUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class WUTCreativeTab {

	public static final DeferredRegister<CreativeModeTab> TAB_TYPES = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, WitherUtils.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_BLOCKS = TAB_TYPES
			.register("tabwu_blocks", () -> {
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
							output.accept(new ItemStack(WUTBlocks.STEEL_BEAM.get()));
							output.accept(new ItemStack(WUTBlocks.STEEL_POLE.get()));
							output.accept(new ItemStack(WUTBlocks.STEEL_RAILING.get()));
							output.accept(new ItemStack(WUTBlocks.STEEL_CATWALK.get()));
							output.accept(new ItemStack(WUTBlocks.ANGEL.get()));
							output.accept(new ItemStack(WUTBlocks.BRICKSDARK.get()));
							output.accept(new ItemStack(WUTBlocks.BRICKSLAVA.get()));
							output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEBLACK.get()));
							output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEGRAY.get()));
							output.accept(new ItemStack(WUTBlocks.SLICEDCONCRETEWHITE.get()));
							output.accept(new ItemStack(WUTBlocks.CREATIVE_GENERATOR.get()));

							output.accept(new ItemStack(WUTBlocks.HYDRO_RESERVOIR.get()));
							output.accept(new ItemStack(WUTBlocks.HYDRO_TURBINE.get()));
						})
						.build();
				return tab;
			});

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_ITEMS = TAB_TYPES.register("tabwu_items",
			() -> {
				CreativeModeTab tab = CreativeModeTab.builder()
						.backgroundTexture(CreativeModeTab.createTextureLocation("wither_blue"))
						.icon(() -> new ItemStack(WUTItems.ICON.get()))
						.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
						.title(Component.translatable("itemGroup.witherutils.items"))
						.displayItems((features, output) -> {

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
							output.accept(new ItemStack(WUTItems.SOULBANK_CASE.get()));
							output.accept(new ItemStack(WUTItems.SOULBANK_BASIC.get()));
							output.accept(new ItemStack(WUTItems.SOULBANK_ADVANCED.get()));
							output.accept(new ItemStack(WUTItems.SOULBANK_ULTRA.get()));
							output.accept(new ItemStack(WUTItems.GUARD_DRONE.get()));
							output.accept(new ItemStack(WUTItems.DRONESTICK.get()));
							output.accept(new ItemStack(WUTItems.COLDSLUSH_BUCKET.get()));

							output.accept(new ItemStack(WUTItems.PENSTOCKPIPE.get()));
						})
						.build();
				return tab;
			});

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_CUTTER = TAB_TYPES
			.register("tabwu_cutter", () -> {
				CreativeModeTab tab = CreativeModeTab.builder()
						.backgroundTexture(CreativeModeTab.createTextureLocation("wither_blue"))
						.icon(() -> new ItemStack(WUTItems.ICON.get()))
						.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
						.title(Component.translatable("itemGroup.witherutils.cutter"))
						.displayItems((features, output) -> {

							for (var entry : WUTBlocks.CTM_CONCRETE.entrySet()) {
								output.accept(new ItemStack(entry.getValue()));
							}
							for (var entry : WUTBlocks.CTM_METAL.entrySet()) {
								output.accept(new ItemStack(entry.getValue()));
							}
							for (var entry : WUTBlocks.CTM_STONE.entrySet()) {
								output.accept(new ItemStack(entry.getValue()));
							}
							for (var entry : WUTBlocks.CTM_WOOD.entrySet()) {
								output.accept(new ItemStack(entry.getValue()));
							}

						})
						.build();
				return tab;
			});

	public static void init(IEventBus bus) {
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
