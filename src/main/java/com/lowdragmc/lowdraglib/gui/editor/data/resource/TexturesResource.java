package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.TexturesResourceContainer;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;

import static com.lowdragmc.lowdraglib.gui.editor.data.resource.TexturesResource.RESOURCE_NAME;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote TextureResource
 */
@RegisterUI(name = RESOURCE_NAME)
public class TexturesResource extends Resource<IGuiTexture> {

    public final static String RESOURCE_NAME = "ldlib.gui.editor.group.textures";

    @Override
    public void buildDefault() {
        data.put("empty", IGuiTexture.EMPTY);
        data.put("border background", ResourceBorderTexture.BORDERED_BACKGROUND);
        for (var wrapper : UIDetector.REGISTER_TEXTURES) {
            data.put("ldlib.gui.editor.register.texture." + wrapper.annotation().name(), wrapper.creator().get());
        }
    }

    @Override
    public String name() {
        return RESOURCE_NAME;
    }

    @Override
    public ResourceContainer<IGuiTexture, ImageWidget> createContainer(ResourcePanel panel) {
        return new TexturesResourceContainer(this, panel);
    }

    @Override
    public Tag serialize(IGuiTexture value) {
        RegisterUI registered = value.getClass().getAnnotation(RegisterUI.class);
        if (registered != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", registered.name());
            CompoundTag data = new CompoundTag();
            PersistedParser.serializeNBT(data, value.getClass(), value);
            tag.put("data", data);
            return tag;
        }
        return null;
    }

    @Override
    public IGuiTexture deserialize(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            var type = tag.getString("type");
            var data = tag.getCompound("data");
            IGuiTexture value = UIDetector.REGISTER_TEXTURES.stream().filter(w -> w.annotation().name().equals(type)).map(UIDetector.Wrapper::creator).findFirst().orElse(() -> IGuiTexture.EMPTY).get();
            PersistedParser.deserializeNBT(data, new HashMap<>(), value.getClass(), value);
            return value;
        }
        return null;
    }
}
