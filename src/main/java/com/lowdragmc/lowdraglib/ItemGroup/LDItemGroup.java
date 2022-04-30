package com.lowdragmc.lowdraglib.ItemGroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LDItemGroup extends ItemGroup {

    protected String domain, id;
    protected Supplier<ItemStack> iconSupplier;

    public LDItemGroup(String domain, String id, Supplier<ItemStack> iconSupplier) {
        super(domain + "." + id);
        this.domain = domain;
        this.id = id;
        this.iconSupplier = iconSupplier;
    }

    public String getDomain() {
        return domain;
    }

    public String getGroupId() {
        return id;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return iconSupplier.get();
    }
}
