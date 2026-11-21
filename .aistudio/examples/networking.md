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
