package geni.withercore.common.network.payload;

import geni.withercore.common.menu.WitherAbstractMenu;
import geni.withercore.common.network.dataslot.ClientboundDataSlotChange;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.packet.EditScreenPacket;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import geni.witherutils.common.world.level.block.screen.ScreenBlockEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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
                if (context.player().containerMenu instanceof WitherAbstractMenu abstractMenu)
                {
                    abstractMenu.serverHandleIncomingPayload(packet.index(), packet.payload());
                }
            }
        });
    }

    public static void handle(EditScreenPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            String url = payload.url();
            if (url == null || url.length() > 2048) return;

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos)) return;
            if (!player.mayBuild()) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > ScreenBlockEntity.REMOTE_MAX_DISTANCE_SQR) return;
            if (!(level.getBlockEntity(pos) instanceof ScreenBlockEntity screen)) return;
            if (!screen.isWallPowered()) return;

            screen.applyImageIdToWall(url.trim());
        });
    }
}
