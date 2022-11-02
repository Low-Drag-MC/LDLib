package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;

/**
 * @author youyihj
 */
public class ProgressTexture implements IGuiTexture {
    protected final IGuiTexture emptyBarArea;
    protected final IGuiTexture filledBarArea;

    protected double progress;
    protected FillDirection fillDirection = FillDirection.LEFT_TO_RIGHT;

    public ProgressTexture(IGuiTexture emptyBarArea, IGuiTexture filledBarArea) {
        this.emptyBarArea = emptyBarArea;
        this.filledBarArea = filledBarArea;
    }

    public void setProgress(double progress) {
        this.progress = Mth.clamp(0.0, progress, 1.0);
    }

    public ProgressTexture setFillDirection(FillDirection fillDirection) {
        this.fillDirection = fillDirection;
        return this;
    }

    @Override
    public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (emptyBarArea != null) {
            emptyBarArea.draw(stack, mouseX, mouseY, x, y, width, height);
        }
        if (filledBarArea != null) {
            float drawnU = (float) fillDirection.getDrawnU(progress);
            float drawnV = (float) fillDirection.getDrawnV(progress);
            float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
            float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
            filledBarArea.drawSubArea(stack, x + drawnU * width, y + drawnV * height, ((int) (width * drawnWidth)), ((int) (height * drawnHeight)),
                    drawnU, drawnV, (drawnWidth * width) / width, ((drawnHeight * height)) / height);
        }
    }

    public static class Auto extends ProgressTexture {

        public Auto(IGuiTexture emptyBarArea, IGuiTexture filledBarArea) {
            super(emptyBarArea, filledBarArea);
        }

        @Override
        public void updateTick() {
            progress = Math.abs(System.currentTimeMillis() % 2000) / 2000.0;
        }
    }

    public enum FillDirection {
        LEFT_TO_RIGHT {
            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        RIGHT_TO_LEFT {
            @Override
            public double getDrawnU(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        UP_TO_DOWN {
            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        },
        DOWN_TO_UP {
            @Override
            public double getDrawnV(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        };

        public double getDrawnU(double progress) {
            return 0.0;
        }

        public double getDrawnV(double progress) {
            return 0.0;
        }

        public double getDrawnWidth(double progress) {
            return progress;
        }

        public double getDrawnHeight(double progress) {
            return progress;
        }
    }
}
