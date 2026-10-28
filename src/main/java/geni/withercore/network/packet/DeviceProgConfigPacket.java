package geni.withercore.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.entity.DeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;

public record DeviceProgConfigPacket(boolean fromBlock, BlockPos pos, boolean isMainHand, CompoundTag tag) implements CustomPacketPayload {

    public static final Type<DeviceProgConfigPacket> TYPE = new Type<>(WitherUtils.loc("device_prog_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeviceProgConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, DeviceProgConfigPacket::fromBlock,
            BlockPos.STREAM_CODEC, DeviceProgConfigPacket::pos,
            ByteBufCodecs.BOOL, DeviceProgConfigPacket::isMainHand,
            ByteBufCodecs.COMPOUND_TAG, DeviceProgConfigPacket::tag,
            DeviceProgConfigPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DeviceProgConfigPacket packet, ServerPlayer player) {
        if (packet.fromBlock()) {
            BlockEntity be = player.level().getBlockEntity(packet.pos());
            if (be instanceof DeviceBlockEntity device) {
                ItemStack progStack = device.getProgStack();
                if (!progStack.isEmpty()) {
                    progStack.set(DataComponents.CUSTOM_DATA, CustomData.of(packet.tag()));
                    device.setChanged();
                    player.level().sendBlockUpdated(packet.pos(), device.getBlockState(), device.getBlockState(), 3);
                }
            }
        } else {
            InteractionHand hand = packet.isMainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(packet.tag()));
            }
        }
    }
}
