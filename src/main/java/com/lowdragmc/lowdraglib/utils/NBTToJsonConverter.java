package com.lowdragmc.lowdraglib.utils;

import com.google.gson.*;
import net.minecraft.nbt.*;

import java.util.Set;

/**
 * @author KilaBash
 * @date 2022/10/22
 * @implNote NBTToJsonConverter
 * @copyright https://github.com/MineMaarten/PneumaticCraft/blob/master/src/pneumaticCraft/common/util/NBTToJsonConverter.java
 */
public class NBTToJsonConverter{
    private final CompoundTag tag;

    public NBTToJsonConverter(CompoundTag tag) {
        this.tag = tag;
    }

    public String convert(boolean pretty) {
        JsonObject json = getObject(this.tag);
        String jsonString = json.toString();
        JsonParser parser = new JsonParser();
        GsonBuilder builder = new GsonBuilder();
        if (pretty) {
            builder.setPrettyPrinting();
        }

        Gson gson = builder.create();
        JsonElement el = parser.parse(jsonString);
        return gson.toJson(el);
    }

    public static JsonObject getObject(CompoundTag tag) {
        Set<String> keys = tag.getAllKeys();
        JsonObject jsonRoot = new JsonObject();
        for (String key : keys) {
            JsonObject keyObject = new JsonObject();
            jsonRoot.add(key, keyObject);
            Tag nbt = tag.get(key);
            keyObject.addProperty("type", nbt.getId());
            if (nbt instanceof CompoundTag) {
                keyObject.add("value", getObject((CompoundTag)nbt));
            } else if (nbt instanceof NumericTag) {
                keyObject.addProperty("value", ((NumericTag)nbt).getAsDouble());
            } else if (nbt instanceof StringTag) {
                keyObject.addProperty("value", nbt.getAsString());
            } else {
                JsonArray array;
                if (nbt instanceof ListTag tagList) {
                    array = new JsonArray();

                    for(int i = 0; i < tagList.size(); ++i) {
                        if (tagList.getElementType() == 10) {
                            array.add(getObject(tagList.getCompound(i)));
                        } else if (tagList.getElementType() == 8) {
                            array.add(new JsonPrimitive(tagList.getString(i)));
                        }
                    }

                    keyObject.add("value", array);
                } else {
                    if (!(nbt instanceof IntArrayTag intArray)) {
                        byte var10002 = nbt.getId();
                        throw new IllegalArgumentException("NBT to JSON converter doesn't support the nbt tag: " + var10002 + ", tag: " + nbt);
                    }

                    array = new JsonArray();

                    for (int i : intArray.getAsIntArray()) {
                        array.add(new JsonPrimitive(i));
                    }

                    keyObject.add("value", array);
                }
            }
        }

        return jsonRoot;
    }
}
