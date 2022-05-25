package com.lowdragmc.lowdraglib.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemStackTypeAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    public static final ItemStackTypeAdapter INSTANCE = new ItemStackTypeAdapter();

    private ItemStackTypeAdapter() { }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return ItemStack.of(TagParser.parseTag(json.getAsString()));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.serializeNBT().toString());
    }
}
