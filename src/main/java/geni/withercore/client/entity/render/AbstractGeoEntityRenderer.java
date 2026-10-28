package geni.withercore.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import geni.withercore.common.helper.TickHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGeoEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    protected final EntityRendererProvider.Context entityRenderProvider;
    protected final EntityRenderDispatcher entityRenderDispatcher;

    protected ItemRenderer itemRenderer;

	protected AbstractGeoEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.entityRenderProvider = context;
		this.entityRenderDispatcher = context.getEntityRenderDispatcher();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight)
	{
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player != null)
        {
            render(pEntity, pEntityYaw, TickHelper.getPartialTick(), pPoseStack, pBufferSource, mc, player.clientLevel, player, pPackedLight);
        }
        
        super.render(pEntity, pEntityYaw, TickHelper.getPartialTick(), pPoseStack, pBufferSource, pPackedLight);
	}
	
    public abstract void render(T pEntity, float pEntityYaw, float tick, PoseStack matrix, MultiBufferSource buffer, Minecraft mc, ClientLevel level, LocalPlayer player, int pPackedLight);
}
