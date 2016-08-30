package com.artchemylab.sliderize;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
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

    private int loadedPageLimit = 1;

    private boolean displayDots = true;
    private boolean alwaysDisplayDots = false;

    private boolean enableTimer = true;

    private Timer timer = null;
    private Handler hanlder = null;
    private Runnable updater = null;
    private long timerDuration = 5000;

    private final String TAG = "sliderizeTag";

    private List<View> dots = new ArrayList<>();
    private int dotsSize = 0;
    private int defaultDotDrawable = 0;
    private int activeDotDrawable = 0;

    public Sliderize(Context ctx) {
        Log.i(TAG, "new Class");
        this.ctx = ctx;
    }

    public Sliderize with(List<String> data) {
        this.data = data;
        this.dotsSize = data.size();
        return this;
    }

    public Sliderize to(RelativeLayout sliderView) {
        this.sliderView = sliderView;
        return this;
    }

    public boolean initiate() {
        Log.i(TAG, "initiate");
        int imagesSize = data.size();

        createSlider ();

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
                    Log.i(TAG, "Type = END to END");

                    break;
                case INFINITE_SLIDE:
                    Log.i(TAG, "Type = Infinite");

                    break;
                default:
                    Log.i(TAG, "Type = Default");
                    enableRestartMode();
                    break;
            }

            if (defaultDotDrawable == 0 || activeDotDrawable == 0) {
                defaultDotDrawable = R.drawable.inactive_icon;
                activeDotDrawable  = R.drawable.active_icon;
            }
            addDots ();

            return true;
        }
        return false;
    }

    /**
     * create viewpager and add it inside the sliderView
     */
    private void createSlider() {
        viewPager = new ViewPager(ctx);
        sliderView.addView(viewPager);
        viewPager.setOffscreenPageLimit(loadedPageLimit);
    }

    private void addDots() {
        if (displayDots) {
            LinearLayout dotsLayout = new LinearLayout(ctx);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            params.setMargins(4, 4, 4, 4);

            for (int x = 0; x < dotsSize; x++) {
                View dot = new View(ctx);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    dot.setBackground(ctx.getResources().getDrawable(defaultDotDrawable));
                } else {
                    dot.setBackgroundDrawable(ctx.getResources().getDrawable(defaultDotDrawable));
                }

                dotsLayout.addView(dot, params);
                dots.add(dot);
            }

            if (currentItem - 1 < 0) {
                dots.get(currentItem).setBackgroundDrawable(ctx.getResources().getDrawable(activeDotDrawable));
            } else {
                dots.get(currentItem - 1).setBackgroundDrawable(ctx.getResources().getDrawable(activeDotDrawable));
            }

            RelativeLayout.LayoutParams dotsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dotsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            dotsParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            sliderView.addView(dotsLayout, dotsParams);

        }
    }

    /**
     * Enable Restart Mode
     * if is last slide animate back to first and vice versa
     */
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

        } else if (imagesSize > 1) {
            if (slideType == RESTART_SLIDE) {
                data.add(0, "first");
                data.add(imagesSize+1, "last");
            }

            Log.i(TAG, "enableMode: size > 1 and slideType == RESTART_SLIDE");
            if (sliderizeAdapter == null)
                sliderizeAdapter = new SliderizeAdapter(ctx, data);

            viewPager.setAdapter(sliderizeAdapter);
            viewPager.setCurrentItem(currentItem);

            enableRestartType();
        }

    }

    /**
     * Enable the how pages are repeated based on Restart Mode
     */
    private void enableRestartType() {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (data.get(position).equals("first")) {
                        currentItem = data.size() - 2;
                        viewPager.setCurrentItem(currentItem, true);
                    } else if (data.get(position).equals("last")) {
                        currentItem = 1;
                        viewPager.setCurrentItem(currentItem, true);
                    } else {
                        currentItem = position;
                    }

                    if (displayDots) {

                        for (int i = 0; i < data.size() - 2; i++) {
                            dots.get(i).setBackgroundDrawable(ctx.getResources().getDrawable(defaultDotDrawable));
                        }
                        if ((data.size()) < 1) {
                            return;
                        } else if (data.size() - 2 >= 1) {
                            if (currentItem - 1 < 0) {
                                dots.get(currentItem).setBackgroundDrawable(ctx.getResources().getDrawable(activeDotDrawable));
                            } else {
                                dots.get(currentItem - 1).setBackgroundDrawable(ctx.getResources().getDrawable(activeDotDrawable));
                            }
                        }
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    /**
     * Change the limit of how many pages are loaded at once.
     * @param limit int: default is 1. bigger the number, more resources you'll need.
     * @return itself.
     */
    public Sliderize changeLoadedPageLimit (int limit) {
        loadedPageLimit = limit;
        return this;
    }

    /**
     * Set the drawables for the dots
     * @param defaultIcon id of the default icon
     * @param activeIcon id of the active icon
     * @return itself
     */
    public Sliderize setDotsDrawables (int defaultIcon, int activeIcon) {
        this.defaultDotDrawable = defaultIcon;
        this.activeDotDrawable  = activeIcon;
        return this;
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
