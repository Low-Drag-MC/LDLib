package com.lowdragmc.lowdraglib.utils.interpolate;

import java.util.function.Consumer;

/**
 * Author: KilaBash
 * Date: 2022/08/26
 */
public class Interpolator {
    private final float from;
    private final float to;
    private final float durationTick;
    private final IEase ease;
    private final Consumer<Number> interpolate;
    private final Consumer<Number> callback;

    private float tick = 0;

    public Interpolator(float from, float to, float durationTick, IEase ease, Consumer<Number> interpolate) {
        this(from, to, durationTick, ease, interpolate, null);
    }

    public Interpolator(float from, float to, float durationTick, IEase ease, Consumer<Number> interpolate, Consumer<Number> callback) {
        this.from = from;
        this.to = to;
        this.durationTick = durationTick;
        this.ease = ease;
        this.interpolate = interpolate;
        this.callback = callback;
    }

    public void reset() {
        tick = 0;
    }

    public boolean isFinish(){
        return tick == durationTick;
    }

    public void update(float tickTime) {
        if (tick >= durationTick) {
            return;
        }
        tick = tickTime;
        if (tick >= durationTick) {
            callback.accept(ease.getInterpolation(tick / durationTick) * (to - from) + from);
        }
        interpolate.accept(ease.getInterpolation(tick / durationTick) * (to - from) + from);
    }
}
