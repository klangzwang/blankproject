package geni.withercore.common.util;

import geni.withercore.common.menu.blockentity.WitherMachineMenu;
import geni.withercore.common.registration.impl.BlockDeferredRegister;
import geni.withercore.common.registration.impl.TileEntityTypeRegistryObject;
import geni.withercore.common.world.level.block.WitherBlock;
import geni.withercore.common.world.level.block.entity.WitherMachineBlockEntity;
import geni.witherutils.client.ClientSetup;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class ClientRegistrationUtil {

    private ClientRegistrationUtil() {
    }

    @SafeVarargs
    public static <T extends BlockEntity> void bindTileEntityRenderer(EntityRenderersEvent.RegisterRenderers event, BlockEntityRendererProvider<T> rendererProvider,
          TileEntityTypeRegistryObject<? extends T>... tileEntityTypeROs) {
        if (tileEntityTypeROs.length == 0) {
            throw new IllegalArgumentException("No renderers provided.");
        } else if (tileEntityTypeROs.length == 1) {
            event.registerBlockEntityRenderer(tileEntityTypeROs[0].get(), rendererProvider);
        } else {
            BlockEntityRendererProvider<T> provider = new BlockEntityRendererProvider<>() {
                @Nullable
                private WeakReference<Context> cachedContext;
                @Nullable
                private WeakReference<BlockEntityRenderer<T>> cachedRenderer;

                @NotNull
                @Override
                public BlockEntityRenderer<T> create(@NotNull Context context) {
                    //If there is a cached context and renderer make use of it, otherwise create one and cache it
                    // this allows us to reduce the number of renderer classes we create
                    BlockEntityRenderer<T> renderer = cachedRenderer == null ? null : cachedRenderer.get();
                    if (cachedContext == null || cachedContext.get() != context || renderer == null) {
                        renderer = rendererProvider.create(context);
                        cachedContext = new WeakReference<>(context);
                        cachedRenderer = new WeakReference<>(renderer);
                    }
                    return renderer;
                }
            };
            for (TileEntityTypeRegistryObject<? extends T> tileTypeRO : tileEntityTypeROs) {
                event.registerBlockEntityRenderer(tileTypeRO.get(), provider);
            }
        }
    }

    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event, PreparableReloadListener... listeners) {
        for (PreparableReloadListener listener : listeners) {
            event.registerReloadListener(listener);
        }
    }

    /*public static <C extends AbstractContainerMenu, U extends Screen & MenuAccess<C>> void registerScreen(RegisterMenuScreensEvent event,
          ContainerTypeRegistryObject<C> type, ScreenConstructor<C, U> factory) {
        event.register(type.get(), factory);
    }

    public static <TILE extends WitherMachineBlockEntity, C extends WitherMachineMenu<TILE>> void registerElectricScreen(RegisterMenuScreensEvent event,
                                                                                                                         ContainerTypeRegistryObject<C> type) {
        ClientRegistrationUtil.<C, GuiElectricMachine<TILE, C>>registerScreen(event, type, GuiElectricMachine::new);
    }*/

    public static void registerKeyBindings(RegisterKeyMappingsEvent event, KeyMapping... keys) {
        for (KeyMapping key : keys) {
            event.register(key);
        }
    }

    public static void setPropertyOverride(Holder<Item> item, ResourceLocation override, ItemPropertyFunction propertyGetter) {
        ItemProperties.register(item.value(), override, propertyGetter);
    }

    public static void registerItemColorHandler(RegisterColorHandlersEvent.Item event, ItemColor itemColor, ItemLike... items) {
        for (ItemLike itemProvider : items) {
            event.register(itemColor, itemProvider);
        }
    }

    public static void registerItemExtensions(RegisterClientExtensionsEvent event, IClientItemExtensions extension, ItemLike... items) {
        for (ItemLike item : items) {
            event.registerItem(extension, item.asItem());
        }
    }

    /*public static void registerBlockExtensions(RegisterClientExtensionsEvent event, BlockDeferredRegister allBlocks) {
        for (Holder<Block> primaryEntry : allBlocks.getPrimaryEntries()) {
            if (primaryEntry.value() instanceof WitherBlock) {
                event.registerBlock(ClientSetup.PARTICLE_HANDLER, primaryEntry);
            }
        }
    }*/

    /*public static void registerFluidExtensions(RegisterClientExtensionsEvent event, FluidDeferredRegister allFluids) {
        for (Holder<FluidType> fluidTypeEntry : allFluids.getFluidTypeEntries()) {
            if (fluidTypeEntry.value() instanceof MekanismFluidType fluidType) {
                event.registerFluidType(new IClientFluidTypeExtensions() {
                    @NotNull
                    @Override
                    public ResourceLocation getStillTexture() {
                        return fluidType.stillTexture;
                    }

                    @NotNull
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return fluidType.flowingTexture;
                    }

                    @Override
                    public ResourceLocation getOverlayTexture() {
                        return fluidType.overlayTexture;
                    }

                    @Nullable
                    @Override
                    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                        return fluidType.renderOverlayTexture;
                    }

                    @NotNull
                    @Override
                    public Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount,
                          @NotNull Vector3f fluidFogColor) {
                        return new Vector3f(MekanismRenderer.getRed(getTintColor()), MekanismRenderer.getGreen(getTintColor()), MekanismRenderer.getBlue(getTintColor()));
                    }

                    @Override
                    public void modifyFogRender(@NotNull Camera camera, @NotNull FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance,
                          float farDistance, @NotNull FogShape shape) {
                        //Copy of logic for water except always treating it as if it was a player who has no water vision
                        // and does not take the biome's closer water fog into account
                        farDistance = 24F;
                        if (farDistance > renderDistance) {
                            farDistance = renderDistance;
                            shape = FogShape.CYLINDER;
                        }
                        RenderSystem.setShaderFogStart(-8);
                        RenderSystem.setShaderFogEnd(farDistance);
                        RenderSystem.setShaderFogShape(shape);
                    }

                    @Override
                    public int getTintColor() {
                        return fluidType.color;
                    }
                }, fluidType);
            }
        }
    }*/
}