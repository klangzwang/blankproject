package geni.witherutils.common.world.level.block.entity;

import geni.withercore.blockentity.WitherMachineBlockEntity;
import geni.withercore.init.WUTBlockEntities;
import geni.withercore.network.CoreNetwork;
import geni.withercore.network.dataslot.NetworkDataSlot;
import geni.withercore.network.packet.DockedDroneTarget;
import geni.withercore.network.packet.SetDockedDronePacket;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class DronePlatformDockingBlockEntity extends WitherMachineBlockEntity {

    private boolean isDocked = false;

    private UUID persistedDroneUuid;

    private int dockedDroneDeliverEntityId = -1;
    private DroneDeliverEntity dockedDrone;

    public DronePlatformDockingBlockEntity(BlockPos pos, BlockState state) {
        super(WUTBlockEntities.DRONE_REDSTONE_EMITTER, pos, state);
        addDataSlot(NetworkDataSlot.BOOL.create(this::isDocked, d -> isDocked = d));
    }

    // ------------------------------------------------------------------
    // Server-seitige API
    // ------------------------------------------------------------------

    /** Wird vom DroneItem aufgerufen, nachdem die Drohne in die Welt gespawnt wurde. */
    public void setDockedDrone(DroneDeliverEntity drone) {
        if (drone == null) {
            clearDockedDrone();
            return;
        }

        this.dockedDrone = drone;
        this.persistedDroneUuid = drone.getUUID();
        this.dockedDroneDeliverEntityId = drone.getId();
        setDocked(true);

        // Client informieren
        if (level != null && !level.isClientSide()) {
            CoreNetwork.sendToAllTracking(
                    new SetDockedDronePacket(worldPosition, DockedDroneTarget.forEntityId(drone.getId())),
                    this
            );
        }
    }

    public void clearDockedDrone() {
        this.dockedDrone = null;
        this.persistedDroneUuid = null;
        this.dockedDroneDeliverEntityId = -1;
        setDocked(false);

        if (level != null && !level.isClientSide()) {
            CoreNetwork.sendToAllTracking(
                    new SetDockedDronePacket(worldPosition, DockedDroneTarget.NONE),
                    this
            );
        }
    }

    // ------------------------------------------------------------------
    // Client-seitige API (vom Packet-Handler aufgerufen)
    // ------------------------------------------------------------------

    public void setClientDockedTarget(DockedDroneTarget target) {
        this.dockedDroneDeliverEntityId = target.entityId();
        this.isDocked = !target.isNone();
        this.dockedDrone = null; // Cache invalidieren, wird lazy neu aufgeloest
    }

    // ------------------------------------------------------------------
    // Gemeinsame API
    // ------------------------------------------------------------------

    public boolean isDocked() {
        return isDocked;
    }

    public void setDocked(boolean docked) {
        if (this.isDocked != docked) {
            this.isDocked = docked;
            setChanged();
        }
    }

    /** Liefert die aktuell angedockte Drohne, oder null. O(1) ueber Entity-ID. */
    public DroneDeliverEntity getDockedDrone() {
        if (level == null || dockedDroneDeliverEntityId < 0) return null;

        // Cache gueltig?
        if (dockedDrone != null && dockedDrone.isAlive() && dockedDrone.getId() == dockedDroneDeliverEntityId) {
            return dockedDrone;
        }

        Entity e = level.getEntity(dockedDroneDeliverEntityId);
        if (e instanceof DroneDeliverEntity d) {
            dockedDrone = d;
            return d;
        }
        dockedDrone = null;
        return null;
    }

    // ------------------------------------------------------------------
    // Tick
    // ------------------------------------------------------------------

    @Override
    public void serverTick() {
        super.serverTick();
        if (level == null) return;

        // Beim ersten Tick nach Weltstart: UUID -> Entity-ID aufloesen
        if (persistedDroneUuid != null && dockedDroneDeliverEntityId < 0
                && level instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(persistedDroneUuid);
            if (e instanceof DroneDeliverEntity drone) {
                this.dockedDrone = drone;
                this.dockedDroneDeliverEntityId = drone.getId();
                setDocked(true);
                CoreNetwork.sendToAllTracking(
                        new SetDockedDronePacket(worldPosition, DockedDroneTarget.forEntityId(drone.getId())),
                        this
                );
            } else {
                // Drohne existiert nicht mehr (gestorben, Welt neu, ...)
                persistedDroneUuid = null;
                setDocked(false);
            }
        }

        // Validierung: Drohne tot / weggenommen -> abdocken
        if (dockedDroneDeliverEntityId >= 0) {
            Entity e = level.getEntity(dockedDroneDeliverEntityId);
            if (!(e instanceof DroneDeliverEntity drone) || !drone.isAlive()) {
                clearDockedDrone();
            } else if (drone.isFlying()) {
                // optional: Drohne fliegt -> abdocken
                clearDockedDrone();
            }
        }
    }

    // ------------------------------------------------------------------
    // NBT
    // ------------------------------------------------------------------

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("isDocked", isDocked);
        if (persistedDroneUuid != null) {
            tag.putUUID("DockedDroneUUID", persistedDroneUuid);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        isDocked = tag.getBoolean("isDocked");
        persistedDroneUuid = tag.hasUUID("DockedDroneUUID") ? tag.getUUID("DockedDroneUUID") : null;
        // dockedDroneDeliverEntityId bleibt -1 -> wird beim ersten serverTick aufgeloest
    }
}