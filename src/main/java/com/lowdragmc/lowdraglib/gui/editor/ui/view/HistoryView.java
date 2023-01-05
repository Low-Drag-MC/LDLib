package com.lowdragmc.lowdraglib.gui.editor.ui.view;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote HistoryView
 */
@RegisterUI(name = "history_view", group = "view")
public class HistoryView extends FloatViewWidget {

    public HistoryView() {
        super(100, 100, 120, 120, false);
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public IGuiTexture getIcon() {
        return Icons.HISTORY.copy();
    }
}
