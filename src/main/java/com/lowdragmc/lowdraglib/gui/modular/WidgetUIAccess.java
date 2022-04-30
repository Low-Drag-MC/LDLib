package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.util.function.Consumer;

public interface WidgetUIAccess {

    void notifySizeChange();

    void notifyWidgetChange();

    boolean attemptMergeStack(ItemStack itemStack, boolean fromContainer, boolean simulate);

    void sendSlotUpdate(SlotWidget slot);

    void sendHeldItemUpdate();

    void writeClientAction(Widget widget, int id, Consumer<PacketBuffer> payloadWriter);

    void writeUpdateInfo(Widget widget, int id, Consumer<PacketBuffer> payloadWriter);

}
