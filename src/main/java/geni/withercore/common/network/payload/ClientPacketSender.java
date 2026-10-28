package geni.withercore.common.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientPacketSender {
    void sendToServer(CustomPacketPayload packet);
}
