package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.annotation.FileSelector;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.StringConfigurator;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.utils.FileUtility;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote ResourceLocationAccessor
 */
@ConfigAccessor
public class ResourceLocationAccessor extends TypesAccessor<ResourceLocation> {

    public ResourceLocationAccessor() {
        super(ResourceLocation.class);
    }

    @Override
    public ResourceLocation defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            return new ResourceLocation(field.getAnnotation(DefaultValue.class).stringValue()[0]);
        }
        return new ResourceLocation(LDLMod.MODID, "default");
    }

    @Override
    public Configurator create(String name, Supplier<ResourceLocation> supplier, Consumer<ResourceLocation> consumer, boolean forceUpdate, Field field) {
//        if (field.isAnnotationPresent(FileSelector.class)) {
//            FileSelector fileSelector = field.getAnnotation(FileSelector.class);
//            cd -> DialogWidget.showFileDialog(this, "select a shader file", path, true,
//                    DialogWidget.suffixFilter(".frag"), r -> {
//                        if (r != null && r.isFile()) {
//                            try {
//                                textFieldWidget.setCurrentString(FileUtility.readInputStream(Files.newInputStream(r.toPath())));
//                            } catch (IOException exception) {
//                                exception.printStackTrace();
//                            }
//                        }
//                    })
//        } else {
            return new StringConfigurator(name, () -> supplier.get().toString(), s -> consumer.accept(new ResourceLocation(s)), defaultValue(field, String.class).toString(), forceUpdate);
//        }
    }
}
