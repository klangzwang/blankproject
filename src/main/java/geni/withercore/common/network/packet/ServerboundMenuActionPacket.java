package geni.withercore.common.network.packet;

import geni.withercore.common.inventory.NearbyStorageHelper;
import geni.withercore.common.menu.WitherAbstractMenu;
import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ServerboundMenuActionPacket(int containerId, ActionType action, int data) implements CustomPacketPayload {

    public enum ActionType {
        WORLD_DROP,
        TRASH,
        QUICK_TRANSFER_NEARBY,
        TOGGLE_PLAYER_INV
    }

    public static final Type<ServerboundMenuActionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "menu_action"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundMenuActionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundMenuActionPacket::containerId,
            NeoForgeStreamCodecs.enumCodec(ActionType.class), ServerboundMenuActionPacket::action,
            ByteBufCodecs.VAR_INT, ServerboundMenuActionPacket::data,
            ServerboundMenuActionPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundMenuActionPacket message, ServerPlayer player) {
        if (player.containerMenu != null && player.containerMenu.containerId == message.containerId()) {
            if (player.containerMenu instanceof WitherAbstractMenu menu) {
                switch (message.action()) {
                    case WORLD_DROP -> menu.dropSlotToWorld(player, message.data());
                    case TRASH -> menu.trashSlotItem(player, message.data());
                    case QUICK_TRANSFER_NEARBY -> {
                        int radius = message.data() > 0 ? message.data() : NearbyStorageHelper.DEFAULT_RADIUS;
                        NearbyStorageHelper.transferToNearbyStorages(player, radius);
                    }
                    case TOGGLE_PLAYER_INV -> menu.setPlayerInvVisible(message.data() == 1);
                }
            }
        }
    }
}
