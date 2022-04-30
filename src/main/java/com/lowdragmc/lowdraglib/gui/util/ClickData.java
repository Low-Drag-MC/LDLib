package com.lowdragmc.lowdraglib.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

public class ClickData {
    public final int button;
    public final boolean isShiftClick;
    public final boolean isCtrlClick;
    public final boolean isRemote;

    private ClickData(int button, boolean isShiftClick, boolean isCtrlClick, boolean isRemote) {
        this.button = button;
        this.isShiftClick = isShiftClick;
        this.isCtrlClick = isCtrlClick;
        this.isRemote = isRemote;
    }

    @OnlyIn(Dist.CLIENT)
    public ClickData() {
        MouseHelper mouseHelper = Minecraft.getInstance().mouseHandler;
        long id = Minecraft.getInstance().getWindow().getWindow();
        this.button = mouseHelper.isLeftPressed() ? 0 : mouseHelper.isRightPressed() ? 1 : 2;
        this.isShiftClick = InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT);
        this.isCtrlClick = InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(id, GLFW.GLFW_KEY_RIGHT_CONTROL);
        this.isRemote = true;
    }

    @OnlyIn(Dist.CLIENT)
    public void writeToBuf(PacketBuffer buf) {
        buf.writeVarInt(button);
        buf.writeBoolean(isShiftClick);
        buf.writeBoolean(isCtrlClick);
    }

    public static ClickData readFromBuf(PacketBuffer buf) {
        int button = buf.readVarInt();
        boolean shiftClick = buf.readBoolean();
        boolean ctrlClick = buf.readBoolean();
        return new ClickData(button, shiftClick, ctrlClick, false);
    }
}
