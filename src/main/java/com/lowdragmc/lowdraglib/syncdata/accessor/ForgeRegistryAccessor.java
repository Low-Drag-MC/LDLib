package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.payload.ForgeRegistryPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ObjectTypedPayload;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class ForgeRegistryAccessor extends SimpleObjectAccessor {
    public ForgeRegistryAccessor() {
        super(ForgeRegistryAccessor.class);
    }

    @Override
    public boolean hasPredicate() {
        return true;
    }

    @SuppressWarnings({"removal", "unchecked"})
    @Override
    public boolean test(Class<?> type) {
        return IForgeRegistryEntry.class.isAssignableFrom(type) && RegistryManager.ACTIVE
                .getRegistry((Class<? super IForgeRegistryEntry<?>>) type) != null;
    }

    @Override
    public ObjectTypedPayload<?> createEmpty() {
        return new ForgeRegistryPayload<>();
    }
}
