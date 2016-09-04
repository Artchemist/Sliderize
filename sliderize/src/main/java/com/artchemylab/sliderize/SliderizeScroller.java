package com.artchemylab.sliderize;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by Artchemist on 9/4/2016.
 */
public class SliderizeScroller extends Scroller {

    private int duration = 500;

    public SliderizeScroller(Context context) {
        super(context);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, this.duration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, this.duration);
    }

    public void setScrollDuration(int duration) {
        this.duration = duration;
    }
}
