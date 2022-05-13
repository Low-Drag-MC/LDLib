package com.lowdragmc.lowdraglib.utils;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/05/13
 * @implNote CustomResourcePack
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomResourcePack implements IPackFinder {
    private final File location;
    private final IPackNameDecorator packSource;

    public CustomResourcePack(File location, IPackNameDecorator packSource, String namespace, String description, int format) {
        this.location = location;
        this.packSource = packSource;

        new File(location, "assets/" + namespace).mkdirs();
        File mcmeta = new File(location, "pack.mcmeta");
        if (!mcmeta.exists()) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            meta.add("pack", pack);
            pack.addProperty("description", description);
            pack.addProperty("pack_format", format);
            FileUtility.saveJson(mcmeta, meta);
        }

    }

    public CustomResourcePack(File location, IPackNameDecorator packSource, String namespace, JsonObject meta) {
        this.location = location;
        this.packSource = packSource;

        new File(location, "assets/" + namespace).mkdirs();
        File mcmeta = new File(location, "pack.mcmeta");
        if (!mcmeta.exists()) {
            FileUtility.saveJson(mcmeta, meta);
        }
    }

    private static final FileFilter RESOURCEPACK_FILTER = (file) -> {
        boolean flag = file.isFile() && file.getName().endsWith(".zip");
        boolean flag1 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return flag || flag1;
    };

    public void loadPacks(Consumer<ResourcePackInfo> pInfoConsumer, ResourcePackInfo.IFactory pInfoFactory) {
        if (!this.location.isDirectory()) {
            this.location.mkdirs();
        }

        if (RESOURCEPACK_FILTER.accept(location)) {
            String s = "file/" + location.getName();
            ResourcePackInfo resourcepackinfo = ResourcePackInfo.create(s, true, this.createSupplier(location), pInfoFactory, ResourcePackInfo.Priority.TOP, this.packSource);
            if (resourcepackinfo != null) {
                pInfoConsumer.accept(resourcepackinfo);
            }
        }

    }

    private Supplier<IResourcePack> createSupplier(File pFile) {
        return pFile.isDirectory() ? () -> new FolderPack(pFile) : () -> new FilePack(pFile);
    }
}
