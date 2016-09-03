package com.artchemylab.sliderize;

import android.content.Context;
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


    public static final int TYPE_RESTART_SLIDE = 1;
    public static final int TYPE_END_SLIDE = 2;
    public static final int TYPE_INFINITE_SLIDE = 3;

    private int slideType = TYPE_RESTART_SLIDE;

    /**
     * Slider View vars
     */
    private Context ctx;
    private RelativeLayout sliderView;
    private LinearLayout dotsLayout;
    private ViewPager viewPager;
    private List<String> data;

    /**
     * Slider adapter
     */
    private SliderizeAdapter sliderizeAdapter = null;

    private int currentItem = 0;

    private int loadedPageLimit = 1;

    /**
     * Dots display vars
     */
    private boolean displayDots = true;
    private boolean alwaysDisplayDots = false;

    /**
     * Timer
     */
    private boolean enableTimer = true;
    private Timer timer = null;
    private Handler hanlder = null;
    private Runnable updater = null;
    private long timerDuration = 5000;

    /**
     * Tag for Logs
     */
    private final String TAG = "sliderizeTag";

    /**
     * Dots List vars
     */
    private List<View> dots = new ArrayList<>();
    private int dotsSize = 0;
    private int defaultDotDrawable = 0;
    private int activeDotDrawable = 0;

    /**
     * Dots size vars
     */
    int dotWidth = 15;
    int dotHeight = 15;

    /**
     * Dots margin vars
     */
    int dotMarginTop = 4;
    int dotMarginRight = dotMarginTop;
    int dotMarginBottom = dotMarginTop;
    int dotMarginLeft = dotMarginTop;

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

        createSlider();

        if (imagesSize == 1) {
            Log.i(TAG, "size == 1");
            currentItem = 0;
            if (!alwaysDisplayDots)
                displayDots = false;
            enableTimer = false;
        } else if (imagesSize > 1 && slideType == TYPE_RESTART_SLIDE) {
            Log.i(TAG, "size > 1 and Type = Restart Slide");
            currentItem = 1;
            enableTimer = true;
        } else if (imagesSize > 1 && slideType == TYPE_END_SLIDE) {
            Log.i(TAG, "size > 1 and Type = End Slide");
            currentItem = 0;
            enableTimer = true;
        } else if (imagesSize > 1 && slideType == TYPE_INFINITE_SLIDE) {
            Log.i(TAG, "size > 1 and Type = Infinite Slide");
            currentItem = 1;
            enableTimer = true;
        }

        if (ctx != null && viewPager != null) {

            switch (slideType) {
                case TYPE_RESTART_SLIDE:
                    Log.i(TAG, "Type = Restart");
                    enableRestartMode();
                    break;
                case TYPE_END_SLIDE:
                    Log.i(TAG, "Type = END to END");
                    enableEndMode();
                    break;
                case TYPE_INFINITE_SLIDE:
                    Log.i(TAG, "Type = Infinite");
                    enableInfiniteMode();
                    break;
                default:
                    Log.i(TAG, "Type = Default");
                    enableRestartMode();
                    break;
            }

            if (defaultDotDrawable == 0 || activeDotDrawable == 0) {
                defaultDotDrawable = R.drawable.inactive_icon;
                activeDotDrawable = R.drawable.active_icon;
            }
            addDots();

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
            dotsLayout = new LinearLayout(ctx);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotWidth, dotHeight);
            params.setMargins(dotMarginLeft, dotMarginTop, dotMarginRight, dotMarginBottom);

            for (int x = 0; x < dotsSize; x++) {
                View dot = new View(ctx);
                dot.setBackgroundDrawable(ctx.getResources().getDrawable(defaultDotDrawable));


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
     * Enable Infinite Mode
     * Going from last to first but give the illusion it is infinite animated.
     */
    private void enableInfiniteMode() {
        final int imagesSize = data.size();

        if (imagesSize == 1) {
            Log.i(TAG, "enableMode: size == 1");
            if (sliderizeAdapter == null)
                sliderizeAdapter = new SliderizeAdapter(ctx, data);

            viewPager.setAdapter(sliderizeAdapter);
            viewPager.setCurrentItem(currentItem);

        } else if (imagesSize > 1) {
            if (slideType == TYPE_INFINITE_SLIDE) {
                data.add(0, data.get(imagesSize - 1));
                data.add(imagesSize + 1, data.get(1));
            }

            Log.i(TAG, "enableMode: size > 1 and slideType == TYPE_INFINITE_SLIDE");
            if (sliderizeAdapter == null)
                sliderizeAdapter = new SliderizeAdapter(ctx, data);

            viewPager.setAdapter(sliderizeAdapter);
            viewPager.setCurrentItem(currentItem);
            viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);

            enableInfiniteType();
        }
    }

    /**
     * Enable how pages are repeated based on Infinite Mode
     */
    private void enableInfiniteType() {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset > 0.99 || positionOffset < 0.001) {
                        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == data.size() -1)
                            viewPager.setCurrentItem(currentItem, false);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                    if (position == 0) {
                        currentItem = data.size() - 2;
                    } else if (position == data.size() - 1) {
                        currentItem = 1;
                    } else {
                        currentItem = position;
                    }

                    if (displayDots) {

                        for (int i = 0; i < data.size() - 2; i++) {
                            dots.get(i).setBackgroundDrawable(ctx.getResources().getDrawable(defaultDotDrawable));
                        }

                        if (data.size() - 2 >= 1) {
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

                    //if (state == ViewPager.SCROLL_STATE_IDLE) {
                        //if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == data.size() -1)
                            //viewPager.setCurrentItem(currentItem, false);
                    //} // /scroll state idle
                }
            });
        }
    }

    /**
     * Enable End to End Mode
     * Default functionality of viewpager
     */
    private void enableEndMode() {

        Log.i(TAG, "enableMode: end to end");
        if (sliderizeAdapter == null)
            sliderizeAdapter = new SliderizeAdapter(ctx, data);

        viewPager.setAdapter(sliderizeAdapter);
        viewPager.setCurrentItem(currentItem);

        enableEndType();

    }

    private void enableEndType() {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentItem = position;
                    if (displayDots) {
                        for (int i = 0; i < data.size(); i++) {
                            dots.get(i).setBackgroundDrawable(ctx.getResources().getDrawable(defaultDotDrawable));
                        }
                        if (data.size() >= 1) {
                            dots.get(currentItem).setBackgroundDrawable(ctx.getResources().getDrawable(activeDotDrawable));
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
            if (slideType == TYPE_RESTART_SLIDE) {
                data.add(0, "first");
                data.add(imagesSize + 1, "last");
            }

            Log.i(TAG, "enableMode: size > 1 and slideType == TYPE_RESTART_SLIDE");
            if (sliderizeAdapter == null)
                sliderizeAdapter = new SliderizeAdapter(ctx, data);

            viewPager.setAdapter(sliderizeAdapter);
            viewPager.setCurrentItem(currentItem);

            enableRestartType();
        }

    }

    /**
     * Enable how pages are repeated based on Restart Mode
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
     *
     * @param limit int: default is 1. bigger the number, more resources you'll need.
     * @return itself.
     */
    public Sliderize changeLoadedPageLimit(int limit) {
        loadedPageLimit = limit;
        return this;
    }

    /**
     * Change the width and height of each dot.
     *
     * @param dotWidth  int: default: 15
     * @param dotHeight int: default: 15
     * @return itself.
     */
    public Sliderize setDotSize(int dotWidth, int dotHeight) {
        this.dotWidth = dotWidth;
        this.dotHeight = dotHeight;
        return this;
    }

    /**
     * Change the margin for all sides of each dot (top, right, bottom, left)
     *
     * @param dotAround int: default 4
     * @return itself.
     */
    public Sliderize setDotMargins(int dotAround) {
        this.dotMarginTop = dotAround;
        this.dotMarginBottom = dotAround;
        this.dotMarginLeft = dotAround;
        this.dotMarginRight = dotAround;
        return this;
    }

    /**
     * Change the margin for horizontal and vertical sides of each dot (top, right, bottom, left)
     *
     * @param dotTopBottom int: default: 4
     * @param dotLeftRight int: default: 4
     * @return
     */
    public Sliderize setDotMargins(int dotTopBottom, int dotLeftRight) {
        this.dotMarginTop = dotTopBottom;
        this.dotMarginBottom = dotTopBottom;
        this.dotMarginLeft = dotLeftRight;
        this.dotMarginRight = dotLeftRight;
        return this;
    }

    /**
     * Change the margin of each dot (top, right, bottom, left)
     *
     * @param dotMarginTop    int: default: 4
     * @param dotMarginRight  int: default: 4
     * @param dotMarginBottom int: default: 4
     * @param dotMarginLeft   int: default: 4
     * @return itself.
     */
    public Sliderize setDotMargins(int dotMarginTop, int dotMarginRight, int dotMarginBottom, int dotMarginLeft) {
        this.dotMarginTop = dotMarginTop;
        this.dotMarginRight = dotMarginRight;
        this.dotMarginBottom = dotMarginBottom;
        this.dotMarginLeft = dotMarginLeft;
        return this;
    }

    /**
     * Set the drawables for the dots
     *
     * @param defaultIcon id of the default icon
     * @param activeIcon  id of the active icon
     * @return itself
     */
    public Sliderize setDotsDrawables(int defaultIcon, int activeIcon) {
        this.defaultDotDrawable = defaultIcon;
        this.activeDotDrawable = activeIcon;
        return this;
    }

    /**
     * Set the type of slider
     *
     * @param type int: one of predefined static vars in Sliderize class. default: Sliderize.TYPE_RESTART_SLIDE
     * @return returns itself.
     */
    public Sliderize setSlideType(int type) {
        this.slideType = type;
        return this;
    }

    /**
     * Set the duration of the timer for the auto play
     *
     * @param milliseconds long: in milliseconds. default: 5000
     * @return returns itself.
     */
    public Sliderize setTimerDuration(long milliseconds) {
        this.timerDuration = milliseconds;
        return this;
    }

    /**
     * Enable dot indicator only if more than one images exist in data
     *
     * @param displayDots boolean: enable dots. default: false
     * @return returns itself.
     */
    public Sliderize setDisplayDots(boolean displayDots) {
        setAlwaysDisplayDots(displayDots, false);
        return this;
    }

    /**
     * Enable dot indicator to always display even if just one image exists in data
     *
     * @param displayDots       boolean: enable dots. default: false
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
        dotsLayout = null;
        sliderView = null;
        sliderizeAdapter = null;
        data.clear();
        data = null;
        viewPager = null;
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
