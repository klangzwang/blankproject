package geni.withercore.network.payload;

import geni.withercore.blockentity.WitherBlockEntity;
import geni.withercore.menu.WitherMenu;
import geni.withercore.network.dataslot.ClientboundDataSlotChange;
import geni.withercore.network.menu.ServerboundSetSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }
    
    @SuppressWarnings("deprecation")
    public void handleDataSlotChange(ClientboundDataSlotChange change, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            var level = context.player().level();
            BlockEntity be = level.getBlockEntity(change.pos());
            if (be instanceof WitherBlockEntity enderBlockEntity)
            {
                var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(change.updateData()), level.registryAccess());
                enderBlockEntity.serverHandleBufferChange(buf);
            }
        });
    }

    public void handleSetSyncSlotDataPacket(ServerboundSetSyncSlotDataPacket packet, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            if (context.player().containerMenu.containerId == packet.containerId())
            {
                if (context.player().containerMenu instanceof WitherMenu enderMenu)
                {
                    enderMenu.serverHandleIncomingPayload(packet.index(), packet.payload());
                }
            }
        });
    }
}
