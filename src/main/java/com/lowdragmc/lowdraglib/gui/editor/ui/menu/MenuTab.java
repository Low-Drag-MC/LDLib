package com.lowdragmc.lowdraglib.gui.editor.ui.menu;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.IRegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote MenuTab
 */
public abstract class MenuTab implements IRegisterUI, INBTSerializable<CompoundTag> {
    private final static Map<String, List<BiConsumer<MenuTab, TreeBuilder.Menu>>> HOOKS = new LinkedHashMap<>();

    protected Editor editor;

    protected MenuTab() {
        this.editor = Editor.INSTANCE;
        if (this.editor == null) {
            throw new RuntimeException("editor is null while creating a menu tab %s".formatted(name()));
        }
    }

    public static void registerMenuHook(String menuName, BiConsumer<MenuTab, TreeBuilder.Menu> consumer) {
        HOOKS.computeIfAbsent(menuName, n -> new ArrayList<>()).add(consumer);
    }

    public TreeBuilder.Menu appendMenu(TreeBuilder.Menu menu) {
        for (var hook : HOOKS.getOrDefault(name(), Collections.emptyList())) {
            hook.accept(this, menu);
        }
        return menu;
    }

    @OnlyIn(Dist.CLIENT)
    public Widget createTabWidget() {
        int width = Minecraft.getInstance().font.width(LocalizationUtils.format(getTranslateKey()));
        var button = new ButtonWidget(0, 0, width + 6, 16, new TextTexture(getTranslateKey()), null)
                .setHoverTexture(ColorPattern.T_WHITE.rectTexture(), new TextTexture(getTranslateKey()));
        button.setOnPressCallback(cd -> {
            var pos = button.getPosition();
            editor.openMenu(pos.x, pos.y + 14, appendMenu(createMenu()));
        });
        return button.setClientSideWidget();
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    abstract protected TreeBuilder.Menu createMenu();
}
