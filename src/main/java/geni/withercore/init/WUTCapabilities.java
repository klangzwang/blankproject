package geni.withercore.init;

import java.util.Optional;

import geni.withercore.blockentity.WitherBlockEntity;
import geni.witherutils.client.gui.console.DevConsoleOverlay;
import geni.witherutils.common.world.item.BatteryItem;
import geni.witherutils.common.world.item.ShieldSteelItem;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockItemHandlerCapability;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockItemHandlerCapability;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class WUTCapabilities {
	
    public static final ICapabilityProvider<?, ?, ?> SIMPLE_PROVIDER = (obj, context) -> obj;

    public static void register(RegisterCapabilitiesEvent event)
    {
    	registerItemCapabilities(event);
    	registerEntityCapabilities(event);
    	registerBlockCapabilities(event);
    }
	
    /*
     * 
     * ENTITY
     * 
     */
    public static void registerEntityCapabilities(RegisterCapabilitiesEvent event)
    {
    }
    
    /*
     * 
     * ITEM
     * 
     */
    public static Optional<IItemHandler> getItemHandler(ItemStack stack)
    {
    	return Optional.ofNullable(stack.getCapability(Capabilities.ItemHandler.ITEM));
    }
    public static Optional<IEnergyStorage> getItemEnergyHandler(ItemStack stack)
    {
        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
    }
    public static Optional<IFluidHandler> getItemFluidHandler(ItemStack stack)
    {
        return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM));
    }
    
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event)
    {
        WUTItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof ShieldSteelItem shield)
            {
                var provider = shield.initEnergyProvider();
                if (provider != null)
                {
                    event.registerItem(Capabilities.EnergyStorage.ITEM, provider, item.get());
                }
            }
        });

        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, ctx) -> new ControllerItemBlockItemHandlerCapability(stack, ControllerItemBlockEntity.ITEMCAPACITY),
                WUTBlocks.RACKITEM_CONTROLLER.asItem());

        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new ControllerFluidBlockItemHandlerCapability(stack, ControllerFluidBlockEntity.FLUIDCAPACITY),
                WUTBlocks.RACKFLUID_CONTROLLER.asItem());

        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, context) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 9),
                WUTItems.CARD.get()
        );

        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (stack, context) -> BatteryItem.createCapability(stack),
                WUTItems.BATTERY.get()
        );
    }

    /*
     * 
     * BLOCK
     * 
     */
    public static Optional<IItemHandler> getBlockItemHandler(BlockEntity blockEntity, Direction side)
    {
        return Optional.ofNullable(blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), side));
    }
    public static Optional<IFluidHandler> getBlockFluidHandler(BlockEntity blockEntity, Direction side)
    {
        return Optional.ofNullable(blockEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), side));
    }
    public static Optional<IEnergyStorage> getBlockEnergyHandler(BlockEntity blockEntity, Direction side)
    {
        return Optional.ofNullable(blockEntity.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), side));
    }
    
    public static void registerBlockCapabilities(RegisterCapabilitiesEvent event)
    {
        WUTBlockEntities.streamBlockEntities().forEach(blockEntity -> {
            if (blockEntity instanceof WitherBlockEntity wbe)
            {
                if (wbe.hasItemCapability())
                {
                    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, wbe.getType(),
                            (object, dir) -> object instanceof WitherBlockEntity be ? (be.getItemHandler(dir) != null ? be.getItemHandler(dir) : be.getPlayerItemHandler()) : null);
                }
                if (wbe.hasFluidCapability())
                {
                    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, wbe.getType(),
                            (object, dir) -> object instanceof WitherBlockEntity be ? be.getFluidHandler(dir) : null);
                }
                if (wbe.hasEnergyCapability())
                {
                    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, wbe.getType(),
                            (object, dir) -> object instanceof WitherBlockEntity be ? be.getEnergyHandler(dir) : null);
                }
            }
        });
    }
}
