package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.core.mixins.accessor.AbstractDisplayViewingScreenAccessor;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.impl.client.gui.screen.AbstractDisplayViewingScreen;
import me.shedaniel.rei.impl.display.DisplaySpec;

import java.util.List;

public final class ReiUtils {

    private ReiUtils() {

    }

    public static List<Display> getCurrentCategoryDisplays(AbstractDisplayViewingScreen displayScreen) {
        return ((AbstractDisplayViewingScreenAccessor) displayScreen).getCategoryMap()
                .get(displayScreen.getCurrentCategory())
                .stream()
                .map(DisplaySpec::provideInternalDisplay)
                .toList();
    }

}
