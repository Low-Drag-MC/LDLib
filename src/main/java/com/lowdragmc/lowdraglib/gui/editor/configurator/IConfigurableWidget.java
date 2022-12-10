package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote IConfigurableWidget
 */
public interface IConfigurableWidget extends IConfigurable, INBTSerializable<CompoundTag> {

    Function<String, UIDetector.Wrapper<RegisterUI, IConfigurableWidget>> CACHE = Util.memoize(type -> {
        for (var wrapper : UIDetector.REGISTER_WIDGETS) {
            if (wrapper.annotation().name().equals(type)) {
                return wrapper;
            }
        }
        return null;
    });

    default Widget widget() {
        return (Widget) this;
    }

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        PersistedParser.serializeNBT(tag, getClass(), this);
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        PersistedParser.deserializeNBT(nbt, new HashMap<>(), getClass(), this);
    }

    default CompoundTag serializeWrapper() {
        var tag = new CompoundTag();
        tag.putString("type", getRegisterUI().name());
        tag.put("data", serializeNBT());
        return tag;
    }

    @Nullable
    static IConfigurableWidget deserializeWrapper(CompoundTag tag) {
        String type = tag.getString("type");
        var wrapper = CACHE.apply(type);
        if (wrapper != null) {
            var child = wrapper.creator().get();
            child.deserializeNBT(tag.getCompound("data"));
            return child;
        }
        return null;
    }
}
