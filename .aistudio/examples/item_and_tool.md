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
