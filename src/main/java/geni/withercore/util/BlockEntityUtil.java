package geni.withercore.util;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import geni.withercore.blockentity.WitherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;

public class BlockEntityUtil
{
    @SuppressWarnings("unchecked")
	public static <T extends BlockEntity> Optional<T> getBlockEntityAt(BlockGetter w, BlockPos pos, Class<T> cls) {
        if (w != null && pos != null) {
            BlockEntity te = w.getBlockEntity(pos);
            if (te != null && cls.isAssignableFrom(te.getClass())) {
                return Optional.of((T) te);
            }
        }
        return Optional.empty();
    }
	
	@SuppressWarnings("unchecked")
	public static <T extends WitherBlockEntity> T getBlockEntity(Class<T> type, BlockGetter world, BlockPos pos)
	{
		var tile = world.getBlockEntity(pos);
		return type.isInstance(tile) ? (T) tile : null;
	}

	public static void dropInventory(BlockEntity tile, IItemHandler inventory)
	{
		var pos = tile.getBlockPos();
		for (var i = 0; i < inventory.getSlots(); i++)
		{
			var stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty())
				Containers.dropItemStack(tile.getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack);
		}
	}

	@SuppressWarnings("resource")
	public static void sendBlockEntityToClients(BlockEntity tile)
	{
		var world = (ServerLevel) tile.getLevel();
		var entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false);
		var packet = ClientboundBlockEntityDataPacket.create(tile, BlockEntity::saveWithoutMetadata);
		for (var e : entities)
			e.connection.send(packet);
	}

    public static void sendUpdatePacket(BlockEntity tileEntity)
    {
        Packet<ClientGamePacketListener> packet = tileEntity.getUpdatePacket();
        if(packet != null)
        {
            sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
        }
    }

    @SuppressWarnings("resource")
	private static void sendUpdatePacket(Level level, BlockPos pos, Packet<ClientGamePacketListener> packet)
    {
        if(level instanceof ServerLevel server)
        {
            List<ServerPlayer> players = server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> player.connection.send(packet));
        }
    }

    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, @NotNull BlockPos pos) {
        return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }
    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, ChunkPos chunkPos) {
        return isChunkLoaded(world, chunkPos.x, chunkPos.z);
    }
    @Contract("null, _, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelAccessor accessor) {
            if (!(accessor instanceof Level level) || !level.isClientSide) {
                return accessor.hasChunk(chunkX, chunkZ);
            }
        }
        return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }
    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelReader reader) {
            if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
                return false;
            }
            return isChunkLoaded(reader, pos);
        }
        return true;
    }
    @Nullable
    @Contract("null, _, _, _ -> null")
    public static <CAP, CONTEXT> CAP getCapability(@Nullable Level level, BlockCapability<CAP, CONTEXT> cap, BlockPos pos, CONTEXT context)
    {
        return getCapability(level, cap, pos, null, null, context);
    }
    @Nullable
    @Contract("null, _, _, _, _, _ -> null")
    public static <CAP, CONTEXT> CAP getCapability(@Nullable Level level, BlockCapability<CAP, CONTEXT> cap, BlockPos pos, @Nullable BlockState state,
          @Nullable BlockEntity tile, CONTEXT context)
    {
        if (!isBlockLoaded(level, pos))
        {
            return null;
        }
        return level.getCapability(cap, pos, state, tile, context);
    }
    @Nullable
    @Contract("null, _ -> null")
    public static BlockEntity getTileEntity(@Nullable BlockGetter world, @NotNull BlockPos pos)
    {
        if (!isBlockLoaded(world, pos))
        {
            return null;
        }
        return world.getBlockEntity(pos);
    }

    @Nullable
    public static Optional<? extends BlockEntity> getTile(BlockGetter world, BlockPos pos) {
		if (world == null || pos == null) {
			return Optional.empty();
		}
        return getTileEntity(world, pos) != null ? Optional.ofNullable(getTileEntity(world, pos)) : Optional.empty();
	}
}
