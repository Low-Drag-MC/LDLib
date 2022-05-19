package com.lowdragmc.lowdraglib.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateTypeAdapterFactory implements TypeAdapterFactory {
    public static final BlockStateTypeAdapterFactory INSTANCE = new BlockStateTypeAdapterFactory();

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (BlockState.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new IBlockStateTypeAdapter(gson);
        }
        return null;
    }

    private static final class IBlockStateTypeAdapter extends TypeAdapter<BlockState> {

        private final Gson gson;

        private IBlockStateTypeAdapter(final Gson gson) {
            this.gson = gson;
        }

        @Override
        public void write(final JsonWriter out, final BlockState value) {
            if (value == null || value.getBlock().getRegistryName() == null) {
                gson.toJson(JsonNull.INSTANCE, out);
                return;
            }
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", value.getBlock().getRegistryName().toString());
            jsonObject.addProperty("meta", value.getBlock().getStateDefinition().getPossibleStates().indexOf(value));
            gson.toJson(jsonObject, out);
        }

        @Override
        public BlockState read(final JsonReader in) {
            final JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
            if (jsonElement.isJsonNull()) return null;
            final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(jsonElement.getAsJsonObject().get("id").getAsString()));
            if (block == null) return null;
            if (jsonElement.getAsJsonObject().has("meta")) {
                final int meta = jsonElement.getAsJsonObject().get("meta").getAsInt();
                return block.getStateDefinition().getPossibleStates().size() > meta ? block.getStateDefinition().getPossibleStates().get(meta) : block.defaultBlockState();
            }
            return block.defaultBlockState();
        }

    }
}
