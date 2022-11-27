package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.networking.IPacket;
import com.lowdragmc.lowdraglib.networking.both.PacketIntLocation;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * a packet that contains payload for managed fields
 */
@NoArgsConstructor
public class SPacketManagedPayload extends PacketIntLocation implements IPacket {
    private CompoundTag extra;
    private BlockEntityType<?> blockEntityType;
    private BitSet changed;

    private ITypedPayload<?>[] payloads;

    public SPacketManagedPayload(BlockEntityType<?> type, BlockPos pos, BitSet changed, ITypedPayload<?>[] payloads, CompoundTag extra) {
        super(pos);
        blockEntityType = type;
        this.changed = changed;
        this.payloads = payloads;
        this.extra = extra;
    }

    public SPacketManagedPayload(CompoundTag tag) {
        super(BlockPos.of(tag.getLong("p")));
        blockEntityType = ForgeRegistries.BLOCK_ENTITIES.getValue(new ResourceLocation(tag.getString("t")));
        changed = BitSet.valueOf(tag.getByteArray("c"));
        ListTag list = tag.getList("l", 10);
        payloads = new ITypedPayload<?>[list.size()];
        for (int i = 0; i < payloads.length; i++) {
            CompoundTag payloadTag = list.getCompound(i);
            byte id = payloadTag.getByte("t");
            var payload = TypedPayloadRegistries.create(id);
            payload.deserializeNBT(payloadTag.get("d"));
            payloads[i] = payload;
        }
        extra = tag.getCompound("e");
    }


    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("p", pos.asLong());
        tag.putString("t", blockEntityType.getRegistryName().toString());
        tag.putByteArray("c", changed.toByteArray());
        ListTag list = new ListTag();
        for (ITypedPayload<?> payload : payloads) {
            CompoundTag payloadTag = new CompoundTag();
            payloadTag.putByte("t", payload.getType());
            var data = payload.serializeNBT();
            if (data != null) {
                payloadTag.put("d", data);
            }
            list.add(payloadTag);
        }
        tag.put("l", list);
        tag.put("e", extra);

        return tag;
    }

    public static SPacketManagedPayload of(IAutoSyncBlockEntity tile, boolean force) {
        BitSet changed = new BitSet();

        Map<ManagedKey, ITypedPayload<?>> payloads = new LinkedHashMap<>();
        var syncedFields = tile.getSyncedFieldRefs();
        for (int i = 0; i < syncedFields.length; i++) {
            var field = syncedFields[i];
            if (force || field.isChanged()) {
                changed.set(i);
                var key = field.getKey();
                payloads.put(key, key.readSyncedField(field));
                field.setChanged(false);
            }
        }
        var extra = new CompoundTag();
        tile.writeCustomSyncData(extra);

        return new SPacketManagedPayload(tile.getBlockEntityType(), tile.getCurrentPos(), changed, payloads.values().toArray(new ITypedPayload<?>[0]), extra);
    }

    public void processPacket(@NotNull IAutoSyncBlockEntity blockEntity) {
        if (blockEntity.getSelf().getType() != blockEntityType) {
            LDLMod.LOGGER.warn("Block entity type mismatch in managed payload packet!");
            return;
        }
        var syncedFields = blockEntity.getSyncedFieldRefs();
        var storage = blockEntity.getSyncStorage();

        var changedKeys = new ManagedKey[changed.cardinality()];
        int j = 0;
        for (int i = 0; i < changed.length(); i++) {
            if (changed.get(i)) {
                var field = syncedFields[i];
                var key = field.getKey();

                boolean hasListener = storage.hasSyncListener(key);
                Object oldValue = null;
                if (hasListener) {
                    oldValue = field.readRaw();
                }
                changedKeys[j] = key;
                key.writeSyncedField(field, payloads[j]);
                if(hasListener) {
                    storage.notifyFieldUpdate(key, field.readRaw(), oldValue);
                }

                j++;
            }
        }
        blockEntity.onDescUpdate(changedKeys);

        blockEntity.readCustomSyncData(extra);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeRegistryIdUnsafe(ForgeRegistries.BLOCK_ENTITIES, blockEntityType);
        buf.writeByteArray(changed.toByteArray());
        for (ITypedPayload<?> payload : payloads) {
            buf.writeByte(payload.getType());
            payload.writePayload(buf);
        }
        buf.writeNbt(extra);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        blockEntityType = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCK_ENTITIES);
        changed = BitSet.valueOf(buffer.readByteArray());
        payloads = new ITypedPayload<?>[changed.cardinality()];
        for (int i = 0; i < payloads.length; i++) {
            byte id = buffer.readByte();
            var payload = TypedPayloadRegistries.create(id);
            payload.readPayload(buffer);
            payloads[i] = payload;
        }
        extra = buffer.readNbt();
    }

    @Override
    public void execute(NetworkEvent.Context handler) {
        if (handler.getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            LDLMod.LOGGER.error("Received managed payload packet on wrong side");
            return;
        }

        Optional<Level> level = LogicalSidedProvider.CLIENTWORLD.get(handler.getDirection().getReceptionSide());
        if (level.isEmpty()) {
            return;
        }
        if (level.get().getBlockEntity(pos) instanceof IAutoSyncBlockEntity autoSyncBlockEntity) {
            processPacket(autoSyncBlockEntity);
        }
    }
}
