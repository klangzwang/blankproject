# WitherUtils Klassenarten Übersicht

Eine Übersicht über die verschiedenen architektonischen Klassenarten im `geni.witherutils`-Projekt, jeweils illustriert durch ein konkretes Beispiel aus der Codebasis inklusive typischem Code-Aufbau.

## Haupt- & Initialisierungsklassen (`WitherUtils.java`)

> \[!IMPORTANT\]
> Steuert den gesamten Lebenszyklus der Mod und fungiert als zentraler Einstiegspunkt für NeoForge.

* **Zweck**: Registrierung von Event-Bus-Listenern, Konfigurationen und Netzwerk-Paketen.

* **Besonderheiten**: Nutzt die `@Mod`-Annotation und verknüpft die `DeferredRegister`-Instanzen mit dem Mod-Event-Bus.

* **Architektur**: Dient als Brücke zwischen der NeoForge-API und den internen Mod-Systemen.

```java
@Mod(WitherUtils.MODID)
public class WitherUtils {
    public static final String MODID = "witherutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WitherUtils(IEventBus modEventBus, ModContainer modContainer) {
        WUTBlocks.BLOCKS.register(modEventBus);
        WUTItems.ITEMS.register(modEventBus);
        WUTBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        WUTMenus.MENUS.register(modEventBus);
        WUTRecipes.SERIALIZERS.register(modEventBus);

        modEventBus.addListener(WUTNetwork::register);
        modContainer.registerConfig(ModConfig.Type.COMMON, WUTConfig.SPEC);
    }
}
```

## Registrierungs- & Setup-Klassen (`WUTBlocks.java`)

> \[!IMPORTANT\]
> Alle Spielobjekte müssen über `DeferredRegister` registriert werden, um Lade-Reihenfolge-Konflikte zu vermeiden.

* **Zweck**: Zentrale Deklaration und Bereitstellung von Blöcken, BlockEntities, Items oder Rezepten.

* **Besonderheiten**: Kapselt die Erstellung von `BlockItem`-Instanzen und die Zuweisung zu Creative-Tabs.

* **Architektur**: Verwendet statische `DeferredHolder`-Referenzen für den typsicheren Zugriff im gesamten Projekt.

```java
public class WUTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WitherUtils.MODID);

    public static final DeferredBlock<BlockCollector> COLLECTOR = registerBlockWithItem(
        "collector",
        () -> new BlockCollector(BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops())
    );

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        WUTItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
```

## Block-Logik-Klassen (`BlockCollector.java`)

> \[!IMPORTANT\]
> Blöcke selbst sind Singletons; zustandsbehaftete Daten dürfen niemals direkt in der Block-Klasse gespeichert werden.

* **Zweck**: Definition der physischen Eigenschaften, Hitboxen, BlockStates und Interaktionen (z. B. Rechtsklick).

* **Besonderheiten**: Implementiert häufig `EntityBlock`, um eine zugehörige `BlockEntity` zu instanziieren.

* **Architektur**: Delegiert komplexe Logik und Tick-Berechnungen an die verknüpfte `BlockEntity`.

```java
public class BlockCollector extends BaseEntityBlock {
    public static final MapCodec<BlockCollector> CODEC = simpleCodec(BlockCollector::new);

    public BlockCollector(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CollectorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider provider) {
            player.openMenu(provider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, WUTBlockEntities.COLLECTOR.get(), CollectorBlockEntity::serverTick);
    }
}
```

## BlockEntity- & Maschinen-Klassen (`CollectorBlockEntity.java`)

> \[!IMPORTANT\]
> Server- und Client-Synchronisation muss explizit über `getUpdatePacket()` und `handleUpdateTag()` erfolgen.

* **Zweck**: Speicherung von Inventaren, Energie (FE), Flüssigkeiten sowie Ausführung der eigentlichen Maschinenlogik pro Tick.

* **Besonderheiten**: Nutzt NeoForge-Capabilities (`IItemHandler`, `IEnergyStorage`) und verwaltet NBT-Serialisierung (`saveAdditional` / `loadAdditional`).

* **Architektur**: Erbt oft von mod-internen Basisklassen (`WitherMachineBlockEntity`), um Energie- und Redstone-Logik wiederzuverwenden.

```java
public class CollectorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public CollectorBlockEntity(BlockPos pos, BlockState state) {
        super(WUTBlockEntities.COLLECTOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CollectorBlockEntity be) {
        if (level.getGameTime() % 20 == 0) {
            be.collectNearbyItems();
        }
    }

    private void collectNearbyItems() {
        // Items im Umkreis einsammeln und in 'inventory' einfügen
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.witherutils.collector");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CollectorMenu(id, playerInv, this);
    }
}
```

## Container- & Menü-Klassen (`CollectorMenu.java`)

> \[!IMPORTANT\]
> Synchronisiert Inventar-Slots und Daten-Variablen sicher zwischen Server-Logik und Client-GUI.

* **Zweck**: Verwaltung der Slot-Positionen (Spieler-Inventar vs. Maschinen-Inventar) und Shift-Klick-Logik (`quickMoveStack`).

* **Besonderheiten**: Arbeitet eng mit `ContainerData` zusammen, um Fortschrittsbalken oder Energiestände ohne eigene Netzwerkpakete zu übertragen.

* **Architektur**: Wird auf dem Server über `MenuProvider` geöffnet und auf dem Client mit dem passenden `Screen` verknüpft.

```java
public class CollectorMenu extends AbstractContainerMenu {
    private final CollectorBlockEntity blockEntity;

    public CollectorMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, (CollectorBlockEntity) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CollectorMenu(int containerId, Inventory playerInv, CollectorBlockEntity blockEntity) {
        super(WUTMenus.COLLECTOR.get(), containerId);
        this.blockEntity = blockEntity;

        // Maschinen-Slots (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.getInventory(), col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerInventory(playerInv);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, WUTBlocks.COLLECTOR.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Standard Shift-Klick Transfer-Logik zwischen Inventar und Maschine
        return ItemStack.EMPTY;
    }
}
```

## GUI- & Screen-Klassen (`CollectorScreen.java`)

> \[!IMPORTANT\]
> Reine Client-Klasse; darf niemals direkt auf Server-Klassen oder Server-Levels zugreifen.

* **Zweck**: Visuelle Darstellung der Benutzeroberfläche, Rendern von Hintergrundtexturen, Tooltips und interaktiven Buttons.

* **Besonderheiten**: Nutzt `GuiGraphics` zum Zeichnen von UI-Elementen und sendet Button-Klicks über Custom-Payloads an den Server.

* **Architektur**: Erbt von `AbstractContainerScreen` und greift für die Anzeige auf die Daten des verknüpften `AbstractContainerMenu` zu.

```java
public class CollectorScreen extends AbstractContainerScreen<CollectorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "textures/gui/collector.png");

    public CollectorScreen(CollectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
```

## Custom-Renderer-Klassen (`RenderCollector.java`)

> \[!IMPORTANT\]
> Performance-kritisch, da die `render`-Methode in jedem einzelnen Frame aufgerufen wird.

* **Zweck**: Dynamisches 3D-Rendering von beweglichen Teilen, schwebenden Items oder Flüssigkeiten innerhalb eines Blocks.

* **Besonderheiten**: Implementiert `BlockEntityRenderer` und arbeitet direkt mit `PoseStack`, `MultiBufferSource` und `BakedModel`.

* **Architektur**: Wird ausschließlich auf dem Client über das `EntityRenderersEvent.RegisterRenderers`-Event registriert.

```java
public class RenderCollector implements BlockEntityRenderer<CollectorBlockEntity> {
    public RenderCollector(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CollectorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.15D, 0.5D);
        
        float rotation = (be.getLevel().getGameTime() + partialTick) * 2.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        Minecraft.getInstance().getItemRenderer().renderStatic(
            new ItemStack(Items.ENDER_PEARL), ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, be.getLevel(), 0
        );
        poseStack.popPose();
    }
}
```

## Item- & Werkzeug-Klassen (`ItemWrench.java`)

> \[!IMPORTANT\]
> Verwendet in modernen Minecraft-Versionen `DataComponents` anstelle von klassischem Item-NBT für instanzspezifische Daten.

* **Zweck**: Abbildung von Werkzeugen, Upgrades, Rüstungen oder Verbrauchsgegenständen mit speziellem Verhalten.

* **Besonderheiten**: Überschreibt Methoden wie `useOn` (Interaktion mit Blöcken) oder `appendHoverText` (dynamische Tooltips).

* **Architektur**: Kapselt spielerzentrierte Aktionen und greift bei Bedarf auf die Capabilities des anvisierten Blocks zu.

```java
public class ItemWrench extends Item {
    public ItemWrench(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, true, context.getPlayer());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.witherutils.wrench").withStyle(ChatFormatting.GRAY));
    }
}
```

## Netzwerk- & Paket-Klassen (`PacketSyncBlockEntity.java`)

> \[!IMPORTANT\]
> Alle über das Netzwerk gesendeten Daten müssen über einen `StreamCodec` serialisiert und validiert werden.

* **Zweck**: Übertragung spezifischer Aktionen (z. B. GUI-Button-Klicks) vom Client zum Server oder visueller Effekte vom Server zum Client.

* **Besonderheiten**: Implementiert `CustomPacketPayload` und definiert strikte Handler für den jeweiligen Netzwerk-Kontext (`IPayloadContext`).

* **Architektur**: Trennt strikt zwischen den Daten (Record-Klasse) und der Ausführungslogik (`handle`-Methode).

```java
public record PacketSyncBlockEntity(BlockPos pos, int mode) implements CustomPacketPayload {
    public static final Type<PacketSyncBlockEntity> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "sync_be"));

    public static final StreamCodec<ByteBuf, PacketSyncBlockEntity> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, PacketSyncBlockEntity::pos,
        ByteBufCodecs.VAR_INT, PacketSyncBlockEntity::mode,
        PacketSyncBlockEntity::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketSyncBlockEntity payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(payload.pos()) instanceof CollectorBlockEntity be) {
                be.setWorkMode(payload.mode());
            }
        });
    }
}
```

## Rezept- & Serializer-Klassen (`AlloyRecipe.java`)

> \[!IMPORTANT\]
> Rezepte werden datengetrieben über JSON geladen und benötigen einen passenden `RecipeSerializer` sowie `Codec`.

* **Zweck**: Definition von benutzerdefinierten Crafting-Mechaniken (z. B. Maschinen-Rezepte mit Energieverbrauch und Verarbeitungszeit).

* **Besonderheiten**: Nutzt `MapCodec` für das Einlesen der JSON-Dateien und `StreamCodec` für die Synchronisation mit dem Client (z. B. für JEI).

* **Architektur**: Implementiert das `Recipe`-Interface inklusive `matches()` für die Überprüfung der Eingangs-Slots.

```java
public record AlloyRecipe(Ingredient inputA, Ingredient inputB, ItemStack output, int energyCost) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level level) {
        return inputA.test(input.getItem(0)) && inputB.test(input.getItem(1));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WUTRecipes.ALLOY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WUTRecipes.ALLOY_TYPE.get();
    }
}
```

## Datengenerierungs-Klassen (`WUTBlockStateProvider.java`)

> \[!IMPORTANT\]
> Wird nicht im regulären Spiel ausgeführt, sondern über den `runData`-Gradle-Task gestartet.

* **Zweck**: Automatische Generierung von JSON-Dateien für BlockStates, Modelle, Loot-Tables, Rezepte und Tags.

* **Besonderheiten**: Verhindert Tippfehler in hunderten JSON-Dateien durch programmatische Erstellung während der Entwicklung.

* **Architektur**: Wird über das `GatherDataEvent` registriert und schreibt die fertigen Ressourcen direkt in den `src/generated`-Ordner.

```java
public class WUTBlockStateProvider extends BlockStateProvider {
    public WUTBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WitherUtils.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(WUTBlocks.COLLECTOR.get(), cubeAll(WUTBlocks.COLLECTOR.get()));
    }
}
```