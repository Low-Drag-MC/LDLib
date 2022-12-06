package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class IGuiTextureAccessor extends CustomObjectAccessor<IGuiTexture>{

    public IGuiTextureAccessor() {
        super(IGuiTexture.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(IGuiTexture value) {
        RegisterUI registered = value.getClass().getAnnotation(RegisterUI.class);
        if (registered != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", registered.name());
            CompoundTag data = new CompoundTag();
            PersistedParser.serializeNBT(data, value.getClass(), value);
            tag.put("data", data);
            return NbtTagPayload.of(tag);
        }
        return PrimitiveTypedPayload.NullPayload.ofNull();
    }

    @Override
    public IGuiTexture deserialize(ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var tag = (CompoundTag)nbtTagPayload.getPayload();
            var type = tag.getString("type");
            var data = tag.getCompound("data");
            IGuiTexture value = UIDetector.REGISTER_TEXTURES.stream().filter(w -> w.annotation().name().equals(type)).map(UIDetector.Wrapper::creator).findFirst().orElse(() -> IGuiTexture.EMPTY).get();
            PersistedParser.serializeNBT(data, value.getClass(), value);
            return value;
        }
        return null;
    }
}
