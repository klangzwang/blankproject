package geni.withercore.common.network.payload;

import geni.withercore.common.menu.WitherAbstractMenu;
import geni.withercore.common.network.dataslot.ServerboundCDataSlotUpdate;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.packet.EmitParticlePacket;
import geni.withercore.common.network.packet.EmitParticleSplinePacket;
import geni.withercore.common.network.packet.EmitParticlesPacket;
import geni.withercore.common.network.packet.PowerGridSyncPacket;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import geni.witherutils.client.power.PowerCableClientCache;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance()
    {
        return INSTANCE;
    }

    public void handleEmitParticle(final EmitParticlePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> clientAddParticle(packet));
    }

    public void handleEmitSplineParticle(final EmitParticleSplinePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> clientAddSplineParticle(packet));
    }

    public void handleEmitParticles(final EmitParticlesPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            for (var particle : packet.particles()) {
                clientAddParticle(particle);
            }
        });
    }

    private void clientAddParticle(EmitParticlePacket packet) {
        Minecraft.getInstance().level.addParticle(packet.particleOptions(), packet.x(), packet.y(), packet.z(),
                packet.xSpeed(), packet.ySpeed(), packet.zSpeed());
    }

    private void clientAddSplineParticle(EmitParticleSplinePacket packet) {
        Minecraft.getInstance().level.addParticle(packet.particleOptions(), packet.startPos().x(), packet.startPos().y(), packet.startPos().z(),
                packet.startVel().x(), packet.startVel().y(), packet.startVel().z());
    }

    @SuppressWarnings("deprecation")
	public void handleDataSlotUpdate(ServerboundCDataSlotUpdate update, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            var level = context.player().level();
            BlockEntity be = level.getBlockEntity(update.pos());
            if (be instanceof WitherBlockEntity enderBlockEntity)
            {
                var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(update.slotData()), level.registryAccess());
                enderBlockEntity.clientHandleBufferSync(buf);
            }
        });
    }

    public void handleSyncSlotDataPacket(ClientboundSyncSlotDataPacket packet, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            if (context.player().containerMenu.containerId == packet.containerId())
            {
                if (context.player().containerMenu instanceof WitherAbstractMenu abstractMenu)
                {
                    for (var pair : packet.payloads())
                    {
                        abstractMenu.clientHandleIncomingPayload(pair.index(), pair.payload());
                    }
                }
            }
        });
    }

    public void handlePowerGridSync(PowerGridSyncPacket packet, IPayloadContext context)
    {
        context.enqueueWork(() -> PowerCableClientCache.setEdges(packet.dimension(), packet.edges()));
    }
}
