package geni.withercore.network.payload;

import geni.withercore.blockentity.WitherBlockEntity;
import geni.withercore.menu.WitherMenu;
import geni.withercore.network.dataslot.ServerboundCDataSlotUpdate;
import geni.withercore.network.menu.ClientboundSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance()
    {
        return INSTANCE;
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
                if (context.player().containerMenu instanceof WitherMenu enderMenu)
                {
                    for (var pair : packet.payloads())
                    {
                        enderMenu.clientHandleIncomingPayload(pair.index(), pair.payload());
                    }
                }
            }
        });
    }
}
