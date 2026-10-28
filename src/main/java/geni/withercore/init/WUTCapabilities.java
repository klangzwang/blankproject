package geni.withercore.init;

import geni.withercore.common.world.entity.WitherEntity;
import geni.withercore.common.world.item.WitherInventoryItem;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockItemHandlerCapability;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockItemHandlerCapability;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

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
    public static Optional<IItemHandler> getEntityItemHandler(Entity entity)
    {
        return Optional.ofNullable(entity.getCapability(Capabilities.ItemHandler.ENTITY, null));
    }
    public static Optional<IEnergyStorage> getEntityEnergyHandler(Entity entity, Direction side)
    {
        return Optional.ofNullable(entity.getCapability(Capabilities.EnergyStorage.ENTITY, side));
    }
    public static Optional<IFluidHandler> getEntityFluidHandler(Entity entity, Direction side)
    {
        return Optional.ofNullable(entity.getCapability(Capabilities.FluidHandler.ENTITY, side));
    }

    public static void registerEntityCapabilities(RegisterCapabilitiesEvent event)
    {
        WUTEntities.ENTITY_TYPES.getEntries().forEach(holder -> {
            EntityType<?> type = holder.get();
            event.registerEntity(Capabilities.ItemHandler.ENTITY, type,
                    (entity, ctx) -> entity instanceof WitherEntity we && we.hasItemCapability() ? we.getItemHandler(null) : null);
            event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, type,
                    (entity, dir) -> entity instanceof WitherEntity we && we.hasItemCapability() ? we.getItemHandler(dir) : null);
            event.registerEntity(Capabilities.FluidHandler.ENTITY, type,
                    (entity, dir) -> entity instanceof WitherEntity we && we.hasFluidCapability() ? we.getFluidHandler(dir) : null);
            event.registerEntity(Capabilities.EnergyStorage.ENTITY, type,
                    (entity, dir) -> entity instanceof WitherEntity we && we.hasEnergyCapability() ? we.getEnergyHandler(dir) : null);
        });
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
        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, ctx) -> new ControllerItemBlockItemHandlerCapability(stack, ControllerItemBlockEntity.ITEMCAPACITY),
                WUTBlocks.RACKITEM_CONTROLLER.asItem());

        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new ControllerFluidBlockItemHandlerCapability(stack, ControllerFluidBlockEntity.FLUIDCAPACITY),
                WUTBlocks.RACKFLUID_CONTROLLER.asItem());

        // RecipeCardItem hat kein Slot-Capability (Rezept wird als NBT gespeichert)


        WUTItems.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof WitherInventoryItem inventoryItem)
            {
                event.registerItem(Capabilities.ItemHandler.ITEM,
                        (stack, ctx) -> inventoryItem.hasItemCapability(stack) ? inventoryItem.getItemHandler(stack) : null,
                        item);
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (stack, ctx) -> inventoryItem.hasFluidCapability(stack) ? inventoryItem.getFluidHandler(stack) : null,
                        item);
                event.registerItem(Capabilities.EnergyStorage.ITEM,
                        (stack, ctx) -> inventoryItem.hasEnergyCapability(stack) ? inventoryItem.getEnergyHandler(stack) : null,
                        item);
            }
        });
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
