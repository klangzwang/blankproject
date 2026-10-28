package geni.withercore.item;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import geni.withercore.init.WUTComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Abstrakte Basisklasse für klick-sensitive Items mit Erkennung von:
 * - Linksklick (passLeftClick)
 * - Rechtsklick (passRightClick)
 * - Block / BlockState, der angeklickt wurde (Bedingung)
 * - Zusatztaste / Modifier (Shift, Ctrl, Alt, None)
 * - UseOnContext (UseItemContext)
 * - Doppelklick-Erkennung (boolean doubleClick)
 * - BlockState-, Model-, Capability- und Biome-Hilfsfunktionen
 * - Toggle-Statusverwaltung (TOGGLE BooleanProperty / DataComponent)
 */
public abstract class WitherClickItem extends WitherItem {

    public static final BooleanProperty TOGGLE = BooleanProperty.create("toggle");

    private static final Map<UUID, ClickRecord> LAST_RIGHT_CLICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_HANDLED_RIGHT_TICK = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_HANDLED_LEFT_TICK = new ConcurrentHashMap<>();

    public WitherClickItem(Item.Properties props)
    {
        super(props.component(WUTComponents.TOGGLE, false));
    }

    /**
     * Erzeugt Item.Properties mit vorregistriertem TOGGLE-DataComponent (Default: false).
     */
    public static Item.Properties defaultToggleProperties(Item.Properties props)
    {
        return props.component(WUTComponents.TOGGLE, false);
    }

    /*
     * =========================================================================
     * Toggle & Aktivierungs-Methoden (ItemStack DataComponent)
     * =========================================================================
     */

    public static boolean isActive(ItemStack stack)
    {
        if (stack == null || stack.isEmpty())
        {
            return false;
        }
        Boolean active = stack.get(WUTComponents.TOGGLE);
        return active != null && active;
    }

    public static void setActive(ItemStack stack, boolean active)
    {
        if (stack != null && !stack.isEmpty())
        {
            stack.set(WUTComponents.TOGGLE, active);
        }
    }

    public static boolean toggleActive(ItemStack stack)
    {
        boolean newState = !isActive(stack);
        setActive(stack, newState);
        return newState;
    }

    /*
     * =========================================================================
     * Klick-Methoden zum Überschreiben in ableitenden Items
     * =========================================================================
     */

    /**
     * Wird beim Linksklick auf einen Block aufgerufen.
     *
     * @param state der angeklickte BlockState ("Bedingung / Block der angeklickt wurde")
     * @param modifier die gehaltene Zusatztaste ("zusatztaste die gehalten werden muss")
     * @param context der Kontext der Interaktion ("UseItemContext")
     */
    public void passLeftClick(BlockState state, ClickModifier modifier, UseOnContext context)
    {
        passLeftClick(state, context);
    }

    /**
     * Überladene Variante für passLeftClick ohne Modifier.
     */
    public void passLeftClick(BlockState state, UseOnContext context)
    {
    }

    /**
     * Wird beim Rechtsklick auf einen Block aufgerufen.
     *
     * @param state der angeklickte BlockState ("Bedingung / Block der angeklickt wurde")
     * @param modifier die gehaltene Zusatztaste ("zusatztaste die gehalten werden muss")
     * @param context der Kontext der Interaktion ("UseItemContext")
     * @param doubleClick true, wenn der Klick ein Doppelklick innerhalb des Zeitfensters war
     */
    public void passRightClick(BlockState state, ClickModifier modifier, UseOnContext context, boolean doubleClick)
    {
        passRightClick(state, context);
    }

    /**
     * Überladene Variante für passRightClick ohne Modifier und Doppelklick-Parameter.
     */
    public void passRightClick(BlockState state, UseOnContext context)
    {
    }

    /*
     * =========================================================================
     * Optionale Bedingungen & Filter (können überschrieben werden)
     * =========================================================================
     */

    /**
     * Prüft, ob die Bedingungen für den Linksklick erfüllt sind.
     * Standardmäßig true. Kann überschrieben werden, um bestimmte Blöcke oder Zustände zu filtern.
     */
    public boolean testLeftClickCondition(BlockState state, ClickModifier modifier, UseOnContext context)
    {
        return true;
    }

    /**
     * Prüft, ob die Bedingungen für den Rechtsklick erfüllt sind.
     * Standardmäßig true. Kann überschrieben werden, um bestimmte Blöcke oder Zustände zu filtern.
     */
    public boolean testRightClickCondition(BlockState state, ClickModifier modifier, UseOnContext context, boolean doubleClick)
    {
        return true;
    }

    /**
     * Definiert die für den Linksklick erforderliche Zusatztaste.
     * Standardmäßig ClickModifier.ANY (keine Einschränkung).
     */
    public ClickModifier getRequiredLeftModifier()
    {
        return ClickModifier.ANY;
    }

    /**
     * Definiert die für den Rechtsklick erforderliche Zusatztaste.
     * Standardmäßig ClickModifier.ANY (keine Einschränkung).
     */
    public ClickModifier getRequiredRightModifier()
    {
        return ClickModifier.ANY;
    }

    /**
     * Gibt an, ob der Rechtsklick zwingend einen Doppelklick voraussetzt.
     * Wenn true, wird passRightClick nur bei einem Doppelklick ausgeführt.
     * Standardmäßig false.
     */
    public boolean requiresRightDoubleClick()
    {
        return false;
    }

    /**
     * Gibt an, ob das Linksklick-Event abgebrochen werden soll (z. B. um Blockabbau zu verhindern).
     * Standardmäßig false.
     */
    public boolean shouldCancelLeftClick(BlockState state, ClickModifier modifier, UseOnContext context)
    {
        return false;
    }

    /**
     * Gibt an, ob das Rechtsklick-Event nach erfolgreicher Ausführung abgebrochen werden soll.
     * Standardmäßig false.
     */
    public boolean shouldCancelRightClick(BlockState state, ClickModifier modifier, UseOnContext context, boolean doubleClick)
    {
        return false;
    }

    /**
     * Zeitfenster für Doppelklicks in Millisekunden (Standard: 400ms).
     */
    public long getDoubleClickThresholdMs()
    {
        return 400L;
    }

    /*
     * =========================================================================
     * Doppelklick-Erkennung
     * =========================================================================
     */

    protected boolean checkRightDoubleClick(Player player, BlockPos pos)
    {
        if (player == null || pos == null)
        {
            return false;
        }

        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis();
        long threshold = getDoubleClickThresholdMs();

        ClickRecord last = LAST_RIGHT_CLICKS.get(uuid);
        if (last != null && pos.equals(last.pos) && (now - last.time) <= threshold && (now - last.time) > 20)
        {
            // Doppelklick erkannt -> zurücksetzen, damit ein 3. Klick nicht erneut als Doppelklick gewertet wird
            LAST_RIGHT_CLICKS.remove(uuid);
            return true;
        }
        else
        {
            LAST_RIGHT_CLICKS.put(uuid, new ClickRecord(pos, now));
            return false;
        }
    }

    private record ClickRecord(BlockPos pos, long time) {}

    /*
     * =========================================================================
     * Doppelausführungs-Schutz
     * =========================================================================
     */

    private void markHandledRightClick(UUID playerId, long tick)
    {
        LAST_HANDLED_RIGHT_TICK.put(playerId, tick);
    }

    private boolean isAlreadyHandledRightClick(UUID playerId, long tick)
    {
        Long lastTick = LAST_HANDLED_RIGHT_TICK.get(playerId);
        return lastTick != null && lastTick == tick;
    }

    private void markHandledLeftClick(UUID playerId, long tick)
    {
        LAST_HANDLED_LEFT_TICK.put(playerId, tick);
    }

    private boolean isAlreadyHandledLeftClick(UUID playerId, long tick)
    {
        Long lastTick = LAST_HANDLED_LEFT_TICK.get(playerId);
        return lastTick != null && lastTick == tick;
    }

    /*
     * =========================================================================
     * Vanilla Item-Methoden
     * =========================================================================
     */

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player != null && isAlreadyHandledRightClick(player.getUUID(), level.getGameTime()))
        {
            return super.useOn(context);
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ClickModifier modifier = ClickModifier.fromPlayer(player);
        boolean doubleClick = player != null && checkRightDoubleClick(player, pos);

        if (getRequiredRightModifier().matches(modifier))
        {
            if (!requiresRightDoubleClick() || doubleClick)
            {
                if (testRightClickCondition(state, modifier, context, doubleClick))
                {
                    if (player != null)
                    {
                        markHandledRightClick(player.getUUID(), level.getGameTime());
                    }
                    passRightClick(state, modifier, context, doubleClick);
                    if (shouldCancelRightClick(state, modifier, context, doubleClick))
                    {
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, Level level, BlockState state, BlockPos pos, LivingEntity entity)
    {
        Player player = (Player) entity;

        if (isAlreadyHandledLeftClick(player.getUUID(), level.getGameTime()))
        {
            return super.mineBlock(itemstack, level, state, pos, player);
        }

        ClickModifier modifier = ClickModifier.fromPlayer(player);

        if (getRequiredLeftModifier().matches(modifier))
        {
            Direction face = player.getDirection().getOpposite();
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
            UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);

            if (testLeftClickCondition(state, modifier, context))
            {
                markHandledLeftClick(player.getUUID(), level.getGameTime());
                passLeftClick(state, modifier, context);
                if (shouldCancelLeftClick(state, modifier, context))
                {
                    return true;
                }
            }
        }

        return super.mineBlock(itemstack, level, state, pos, player);
    }

    /*
     * =========================================================================
     * EventBus Subscriber für vorzeitiges Abfangen von Links- und Rechtsklicks
     * =========================================================================
     */

    public void handleLeftClickEvent(PlayerInteractEvent.LeftClickBlock event)
    {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START)
        {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        if (player == null)
        {
            return;
        }

        BlockState state = level.getBlockState(pos);
        ClickModifier modifier = ClickModifier.fromPlayer(player);

        if (!getRequiredLeftModifier().matches(modifier))
        {
            return;
        }

        Direction face = event.getFace() != null ? event.getFace() : Direction.UP;
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
        UseOnContext context = new UseOnContext(player, event.getHand(), hit);

        if (!testLeftClickCondition(state, modifier, context))
        {
            return;
        }

        markHandledLeftClick(player.getUUID(), level.getGameTime());
        passLeftClick(state, modifier, context);

        if (shouldCancelLeftClick(state, modifier, context))
        {
            event.setCanceled(true);
        }
    }

    public void handleRightClickEvent(PlayerInteractEvent.RightClickBlock event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        if (player == null)
        {
            return;
        }

        BlockState state = level.getBlockState(pos);
        ClickModifier modifier = ClickModifier.fromPlayer(player);
        boolean doubleClick = checkRightDoubleClick(player, pos);

        if (requiresRightDoubleClick() && !doubleClick)
        {
            return;
        }

        if (!getRequiredRightModifier().matches(modifier))
        {
            return;
        }

        Direction face = event.getFace() != null ? event.getFace() : Direction.UP;
        BlockHitResult hit = event.getHitVec() != null ? event.getHitVec() : new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
        UseOnContext context = new UseOnContext(player, event.getHand(), hit);

        if (!testRightClickCondition(state, modifier, context, doubleClick))
        {
            return;
        }

        markHandledRightClick(player.getUUID(), level.getGameTime());
        passRightClick(state, modifier, context, doubleClick);

        if (shouldCancelRightClick(state, modifier, context, doubleClick))
        {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    /*
     * =========================================================================
     * BlockState & Model Hilfsfunktionen
     * =========================================================================
     */

    /**
     * Gibt den aktuellen BlockState des angeklickten Blocks zurück.
     * Kann z. B. verwendet werden, um das Aussehen zu wechseln oder Eigenschaften zu prüfen.
     */
    public BlockState getBlockStateFromClick(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return null;
        }
        return context.getLevel().getBlockState(context.getClickedPos());
    }

    public BlockState getBlockStateFromClick(Level level, BlockPos pos)
    {
        if (level == null || pos == null)
        {
            return null;
        }
        return level.getBlockState(pos);
    }

    /**
     * Gibt die ModelResourceLocation des BlockStates zurück.
     */
    @OnlyIn(Dist.CLIENT)
    public ModelResourceLocation getModelResourceLocation(BlockState state)
    {
        if (state == null)
        {
            return null;
        }
        return BlockModelShaper.stateToModelLocation(state);
    }

    @OnlyIn(Dist.CLIENT)
    public ModelResourceLocation getModelResourceLocation(UseOnContext context)
    {
        return getModelResourceLocation(getBlockStateFromClick(context));
    }

    /**
     * Gibt das BakedModel des angeklickten BlockStates zurück.
     */
    @OnlyIn(Dist.CLIENT)
    public BakedModel getBakedModel(BlockState state)
    {
        if (state == null)
        {
            return null;
        }
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getBakedModel(UseOnContext context)
    {
        return getBakedModel(getBlockStateFromClick(context));
    }

    /**
     * Gibt das Block-Model des angeklickten Blocks / BlockStates zurück.
     */
    @OnlyIn(Dist.CLIENT)
    public BakedModel getBlockModel(BlockState state)
    {
        return getBakedModel(state);
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getBlockModel(UseOnContext context)
    {
        return getBakedModel(context);
    }

    /*
     * =========================================================================
     * Capability Hilfsfunktionen (Item, Energy, Fluid)
     * =========================================================================
     */

    public enum ClickCapabilityType {
        ITEM,
        ENERGY,
        FLUID;

        public static ClickCapabilityType fromString(String name)
        {
            if (name == null)
            {
                return null;
            }
            return switch (name.trim().toUpperCase()) {
                case "ITEM", "ITEMS", "ITEMHANDLER" -> ITEM;
                case "ENERGY", "POWER", "FE", "FORGE_ENERGY" -> ENERGY;
                case "FLUID", "FLUIDS", "FLUIDHANDLER", "LIQUID" -> FLUID;
                default -> null;
            };
        }
    }

    /**
     * Universelle getCapability-Methode mit Enum-Parameter (ITEM, ENERGY, FLUID).
     */
    public Optional<?> getCapability(UseOnContext context, ClickCapabilityType type)
    {
        if (context == null || type == null)
        {
            return Optional.empty();
        }
        return switch (type) {
            case ITEM -> getItemHandler(context);
            case ENERGY -> getEnergyStorage(context);
            case FLUID -> getFluidHandler(context);
        };
    }

    /**
     * Universelle getCapability-Methode mit String-Parameter ("Item", "Energy", "Fluid").
     */
    public Optional<?> getCapability(UseOnContext context, String type)
    {
        ClickCapabilityType capType = ClickCapabilityType.fromString(type);
        if (capType == null)
        {
            return Optional.empty();
        }
        return getCapability(context, capType);
    }

    public Optional<IItemHandler> getItemHandler(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(context.getLevel().getCapability(
                Capabilities.ItemHandler.BLOCK,
                context.getClickedPos(),
                context.getClickedFace()
        ));
    }

    public Optional<IItemHandler> getItemHandler(Level level, BlockPos pos, Direction side)
    {
        if (level == null || pos == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side));
    }

    public Optional<IEnergyStorage> getEnergyStorage(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(context.getLevel().getCapability(
                Capabilities.EnergyStorage.BLOCK,
                context.getClickedPos(),
                context.getClickedFace()
        ));
    }

    public Optional<IEnergyStorage> getEnergyStorage(Level level, BlockPos pos, Direction side)
    {
        if (level == null || pos == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side));
    }

    public Optional<IFluidHandler> getFluidHandler(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(context.getLevel().getCapability(
                Capabilities.FluidHandler.BLOCK,
                context.getClickedPos(),
                context.getClickedFace()
        ));
    }

    public Optional<IFluidHandler> getFluidHandler(Level level, BlockPos pos, Direction side)
    {
        if (level == null || pos == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side));
    }

    /*
     * =========================================================================
     * Biome Hilfsfunktionen
     * =========================================================================
     */

    public Holder<Biome> getBiome(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return null;
        }
        return context.getLevel().getBiome(context.getClickedPos());
    }

    public Holder<Biome> getBiome(Level level, BlockPos pos)
    {
        if (level == null || pos == null)
        {
            return null;
        }
        return level.getBiome(pos);
    }

    public Optional<ResourceKey<Biome>> getBiomeKey(UseOnContext context)
    {
        Holder<Biome> holder = getBiome(context);
        return holder != null ? holder.unwrapKey() : Optional.empty();
    }

    public ResourceLocation getBiomeResourceLocation(UseOnContext context)
    {
        return getBiomeKey(context).map(ResourceKey::location).orElse(null);
    }

    public float getBiomeTemperature(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return 0.0F;
        }
        return context.getLevel().getBiome(context.getClickedPos()).value().getBaseTemperature();
    }

    public boolean hasBiomePrecipitation(UseOnContext context)
    {
        Holder<Biome> holder = getBiome(context);
        return holder != null && holder.value().hasPrecipitation();
    }

    public boolean isRainingAt(UseOnContext context)
    {
        if (context == null || context.getLevel() == null || context.getClickedPos() == null)
        {
            return false;
        }
        return context.getLevel().isRainingAt(context.getClickedPos());
    }

    /*
     * =========================================================================
     * Spieler Hilfsfunktionen (UUID, Position)
     * =========================================================================
     */

    public UUID getPlayerUUID(UseOnContext context)
    {
        Player player = context != null ? context.getPlayer() : null;
        return player != null ? player.getUUID() : null;
    }

    public UUID getPlayerUUID(Player player)
    {
        return player != null ? player.getUUID() : null;
    }

    public Vec3 getPlayerLocation(UseOnContext context)
    {
        Player player = context != null ? context.getPlayer() : null;
        return player != null ? player.position() : null;
    }

    public Vec3 getPlayerLocation(Player player)
    {
        return player != null ? player.position() : null;
    }

    public BlockPos getPlayerBlockPos(UseOnContext context)
    {
        Player player = context != null ? context.getPlayer() : null;
        return player != null ? player.blockPosition() : null;
    }

    @EventBusSubscriber
    public static class ClickEventHandler
    {
        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
        {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof WitherClickItem clickItem)
            {
                clickItem.handleLeftClickEvent(event);
            }
        }

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
        {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof WitherClickItem clickItem)
            {
                clickItem.handleRightClickEvent(event);
            }
        }
    }
}
