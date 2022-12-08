package com.lowdragmc.lowdraglib.gui.editor.data;


import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author KilaBash
 * @date 2022/12/4
 * @implNote Project
 */
public record Project(
        Resources resources,
        WidgetGroup root) {

    public static Project newEmptyProject() {
        return new Project(Resources.defaultResource(),
                (WidgetGroup) new WidgetGroup(30, 30, 200, 200).setBackground(ResourceBorderTexture.BORDERED_BACKGROUND));
    }

    public static Project fromNBT(CompoundTag tag) {
        WidgetGroup root = new WidgetGroup();
        root.deserializeNBT(tag.getCompound("root"));
        return new Project(Resources.fromNBT(tag.getCompound("resources")), root);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("resources", resources.serializeNBT());
        tag.put("root", root.serializeNBT());
        return tag;
    }

}
