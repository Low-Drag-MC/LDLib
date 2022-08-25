package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.rei.FakeSlotEntryWidget;
import com.lowdragmc.lowdraglib.rei.ModularWrapperWidget;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.jeicompat.wrap.JEIDisplaySetup;
import me.shedaniel.rei.jeicompat.wrap.JEIRecipeSlot;
import me.shedaniel.rei.jeicompat.wrap.JEIWrappedCategory;
import me.shedaniel.rei.jeicompat.wrap.JEIWrappedDisplay;
import me.shedaniel.math.Rectangle;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.LazyLoadedValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = JEIWrappedCategory.class, remap = false)
public abstract class ReiJEIWrappedCategoryMixin {

    @SuppressWarnings({"deprecation", "unchecked"})
    @Inject(
            method = "setupDisplay(Lme/shedaniel/rei/jeicompat/wrap/JEIDisplaySetup$Result;Lmezz/jei/api/recipe/category/IRecipeCategory;Lme/shedaniel/rei/jeicompat/wrap/JEIWrappedDisplay;Lme/shedaniel/math/Rectangle;Lnet/minecraft/util/LazyLoadedValue;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/jeicompat/wrap/JEIDisplaySetup;addTo(Ljava/util/List;Lme/shedaniel/math/Rectangle;Lme/shedaniel/rei/jeicompat/wrap/JEIDisplaySetup$Result;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    private static <T> void injectRecipeSetup(
            JEIDisplaySetup.Result result,
            IRecipeCategory<T> category,
            JEIWrappedDisplay<T> display,
            Rectangle bounds,
            LazyLoadedValue<IDrawable> backgroundLazy,
            CallbackInfoReturnable<List<Widget>> cir,
            List<Widget> widgets
    ) {
        if (category instanceof ModularUIRecipeCategory) {
            JEIWrappedDisplay<ModularWrapper<?>> wrapperDisplay = (JEIWrappedDisplay<ModularWrapper<?>>) display;
            ModularWrapper<?> wrapper = wrapperDisplay.getBackingRecipe();
            var flatVisibleWidgetCollection = wrapper.modularUI.getFlatWidgetCollection();
            //wrap the LDLib's slot widget
            for (JEIRecipeSlot reiWrapperSlot : result.slots) {
                if (reiWrapperSlot.slot != null && reiWrapperSlot.isVisible()) {
                    var widget = flatVisibleWidgetCollection.get(reiWrapperSlot.slot.getBounds().x + 1);
                    widgets.add(new FakeSlotEntryWidget(widget));
                }
            }
            widgets.add(new ModularWrapperWidget(wrapper, bounds));

            //The original logic
            if (result.shapelessData.shapeless) {
                Point shapelessPoint = result.shapelessData.pos;
                if (shapelessPoint != null) {
                    widgets.add(Widgets.createShapelessIcon(new Point(shapelessPoint.x + 9, shapelessPoint.y - 1)));
                } else {
                    widgets.add(Widgets.createShapelessIcon(bounds));
                }
            }
            cir.setReturnValue(widgets);
        }
    }

}
