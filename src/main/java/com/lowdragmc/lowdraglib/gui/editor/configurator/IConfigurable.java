package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.ConfiguratorParser;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote IConfigurable
 *
 * You may need to register it as a {@link com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI}.
 * <br>
 * to de/serialize it.
 */
public interface IConfigurable {

    default void buildConfigurator(ConfiguratorGroup father) {
        ConfiguratorParser.createConfigurators(father, new HashMap<>(), getClass(), this);
    }

    default boolean isRegisterUI() {
        return getClass().isAnnotationPresent(RegisterUI.class);
    }

    @Nullable
    default RegisterUI getRegisterUI() {
        return getClass().getAnnotation(RegisterUI.class);
    }

}
