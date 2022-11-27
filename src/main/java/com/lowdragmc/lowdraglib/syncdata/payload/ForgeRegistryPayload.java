package com.lowdragmc.lowdraglib.syncdata.payload;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ForgeRegistryPayload<T extends IForgeRegistryEntry<T>> extends ObjectTypedPayload<T> {
    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeRegistryId(payload);
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = buf.readRegistryId();
    }

    @SuppressWarnings("removal")
    @Override
    public @Nullable Tag serializeNBT() {
        Class<T> regType = Objects.requireNonNull(payload, "Cannot write a null registry entry!").getRegistryType();
        IForgeRegistry<T> retrievedRegistry = RegistryManager.ACTIVE.getRegistry(regType);
        Preconditions.checkArgument(retrievedRegistry != null, "Cannot write registry id for an unknown registry type: %s", regType.getName());
        ResourceLocation name = retrievedRegistry.getRegistryName();
        Preconditions.checkArgument(retrievedRegistry.containsValue(payload), "Cannot find %s in %s", payload.getRegistryName() != null ? payload.getRegistryName() : payload, name);
        ForgeRegistry<T> reg = (ForgeRegistry<T>) retrievedRegistry;
        int id = reg.getID(payload);
        CompoundTag tag = new CompoundTag();
        tag.putString("registry", name.toString());
        tag.putInt("id", id);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        CompoundTag compound = (CompoundTag) tag;
        ResourceLocation name = new ResourceLocation(compound.getString("registry"));
        int id = compound.getInt("id");
        ForgeRegistry<T> retrievedRegistry = RegistryManager.ACTIVE.getRegistry(name);
        Preconditions.checkArgument(retrievedRegistry != null, "Cannot read registry id for an unknown registry: %s", name);
        payload = retrievedRegistry.getValue(id);
    }
}
