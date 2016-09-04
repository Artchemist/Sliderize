package com.artchemylab.sliderize;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;

/**
 * Created by Artchemist on 9/4/2016.
 */
public class SliderizeViewPager extends ViewPager {

    private SliderizeScroller mScroller = null;

    public SliderizeViewPager(Context context) {
        super(context);
        initiate();
    }

    private void initiate () {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            this.mScroller = new SliderizeScroller(getContext());
            scroller.set(this, this.mScroller);
        } catch (Exception ignored) {
        }
    }

    public void setScrollDuration (int duration) {
        this.mScroller.setScrollDuration(duration);
    }
}
