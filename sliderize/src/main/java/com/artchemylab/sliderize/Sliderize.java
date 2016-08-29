package com.artchemylab.sliderize;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Timer;

/**
 * Sliderize: Slider Library
 * Two Types: End, Repeat
 * Two Style Transitions: slide, Fade
 * Created by Artchemist on 8/10/2016.
 */
public class Sliderize {

    public static final int RESTART_SLIDE = 1;
    public static final int END_SLIDE = 2;
    public static final int INFINITE_SLIDE = 3;

    private int slideType = RESTART_SLIDE;

    private Context ctx;
    private RelativeLayout sliderView;
    private LinearLayout dotsView;
    private ViewPager viewPager;
    private List<String> data;

    private SliderizeAdapter sliderizeAdapter = null;

    private int currentItem = 0;

    private boolean displayDots = true;
    private boolean alwaysDisplayDots = false;

    private boolean enableTimer = true;

    private Timer timer = null;
    private Handler hanlder = null;
    private Runnable updater = null;
    private long timerDuration = 5000;

    private final String TAG = "sliderizeTag";

    //TODO: DELETE METHOD
    public Sliderize(Context ctx, ViewPager viewPager, List<String> data) {
        Log.i(TAG, "new Class");
        this.ctx = ctx;
        this.viewPager = viewPager;
        this.data = data;
    }

    public Sliderize(Context ctx) {
        Log.i(TAG, "new Class");
        this.ctx = ctx;
    }

    public Sliderize with(List<String> data) {
        this.data = data;
        return this;
    }

    public Sliderize to(RelativeLayout sliderView) {
        this.sliderView = sliderView;
        return this;
    }

    public boolean initiate() {
        Log.i(TAG, "initiate");
        int imagesSize = data.size();

        createSlider();

        if (imagesSize == 1) {
            Log.i(TAG, "size == 1");
            currentItem = 0;
            if (!alwaysDisplayDots)
                displayDots = false;
            enableTimer = false;
        } else if (imagesSize > 1 && slideType == RESTART_SLIDE) {
            Log.i(TAG, "size > 1");
            currentItem = 1;
            enableTimer = true;
        }

        if (ctx != null && viewPager != null) {

            switch (slideType) {
                case RESTART_SLIDE:
                    Log.i(TAG, "Type = Restart");
                    enableRestartMode();
                    break;
                case END_SLIDE:

                    break;
                case INFINITE_SLIDE:

                    break;
                default:
                    Log.i(TAG, "Type = Default");
                    enableRestartMode();
                    break;
            }
            return true;

        }

        return false;
    }

    /**
     * create viewpager and add it inside the 
     */
    private void createSlider() {
        viewPager = new ViewPager(ctx);
        sliderView.addView(viewPager);
    }

    private void enableRestartMode() {

        final int imagesSize = data.size();

        if (imagesSize == 1) {
            Log.i(TAG, "enableMode: size == 1");
            if (sliderizeAdapter == null)
                sliderizeAdapter = new SliderizeAdapter(ctx, data);

            viewPager.setAdapter(sliderizeAdapter);
            viewPager.setCurrentItem(currentItem);

//            if (enableTimer) {
//                if (timer == null)
//                    timer = new Timer();
//                if (hanlder == null)
//                    hanlder = new Handler();
//
//                updater = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (currentItem >= 1 && currentItem <= imagesSize - 2) {
//                            viewPager.setCurrentItem(currentItem++, true);
//                        } else if (currentItem > imagesSize - 2) {
//                            currentItem = 1;
//                            viewPager.setCurrentItem(currentItem, false);
//                        } else if (currentItem < 1) {
//                            currentItem = imagesSize - 2;
//                            viewPager.setCurrentItem(currentItem, true);
//                        }
//                    }
//                };
//
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        hanlder.post(updater);
//                    }
//                }, timerDuration);
//
//            }

        }

    }

    /**
     * Set the type of slider
     * @param type int: one of predefined static vars in Sliderize class. default: Sliderize.RESTART_SLIDE
     * @return returns itself.
     */
    public Sliderize setSlideType(int type) {
        this.slideType = type;
        return this;
    }

    /**
     * Set the duration of the timer for the auto play
     * @param milliseconds long: in milliseconds. default: 5000
     * @return returns itself.
     */
    public Sliderize setTimerDuration(long milliseconds) {
        this.timerDuration = milliseconds;
        return this;
    }

    /**
     * Enable dot indicator only if more than one images exist in data
     * @param displayDots boolean: enable dots. default: false
     * @return returns itself.
     */
    public Sliderize setDisplayDots(boolean displayDots) {
        setAlwaysDisplayDots(displayDots, false);
        return this;
    }

    /**
     * Enable dot indicator to always display even if just one image exists in data
     * @param displayDots boolean: enable dots. default: false
     * @param alwaysDisplayDots boolean: always show dots even if only one dot exists. default: false
     * @return returns itself.
     */
    public Sliderize setAlwaysDisplayDots(boolean displayDots, boolean alwaysDisplayDots) {
        this.displayDots = displayDots;
        this.alwaysDisplayDots = alwaysDisplayDots;
        return this;
    }


    /**
     * Release all resources on Activity's on Destroy
     */
    public void onDestroy() {
        clearTimer();
    }

    /**
     * Release some resources on Activity's on Destroy
     * Compine with onResume
     */
    public void onPause() {
        clearTimer();
    }

    /**
     * Re-initiate some resources on Activity's on Destroy
     * Combine with onPause
     */
    public void onResume() {
        //TODO
    }

    /**
     * Release resources on Fragment's on Detach
     */
    public void onDetach() {
        clearTimer();
    }

    private void clearTimer() {
        if (timer != null)
            timer.cancel();
        updater = null;
        hanlder = null;
    }

}
