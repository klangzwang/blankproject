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
