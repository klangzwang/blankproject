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
