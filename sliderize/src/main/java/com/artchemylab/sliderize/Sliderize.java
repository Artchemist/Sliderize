package com.artchemylab.sliderize;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

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
    private SliderizeViewPager viewPager;
    private List<String> data;
    private int slideDuration = 500;

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
    private Handler hanlder = null;
    private Runnable updater = null;
    private long timerDuration = 5000;
    private boolean reverseEndSlider = false;

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
    private int dotWidth = 15;
    private int dotHeight = 15;

    /**
     * Dots margin vars
     */
    private int dotMarginTop = 4;
    private int dotMarginRight = dotMarginTop;
    private int dotMarginBottom = dotMarginTop;
    private int dotMarginLeft = dotMarginTop;

    /**
     * Slider effect vars
     */
    public static final int EFFECT_DEFAULT_SLIDE = 1;
    public static final int EFFECT_FADE_SLIDE = 2;

    private int effectStyle = EFFECT_FADE_SLIDE;

    private OnSlideEventListener onSlideEventListener;

    /**
     * default Constructor
     *
     * @param ctx Context: get the context when it is called
     */
    public Sliderize(Context ctx) {
        Log.i(TAG, "new Class");
        this.ctx = ctx;
    }

    /**
     * Pass to the slider the array of images you want to be displayed
     *
     * @param data List<String>: (url for images)
     * @return itself.
     */
    public Sliderize with(List<String> data) {
        this.data = data;
        this.dotsSize = data.size();
        return this;
    }

    /**
     * Pass to the slider the view it will use
     *
     * @param sliderView RelativeLayout
     * @return itself.
     */
    public Sliderize to(RelativeLayout sliderView) {
        this.sliderView = sliderView;
        return this;
    }

    /**
     * After called the required methods for your types and styles call initiate to create the slider
     *
     * @return returns true if the was no problem with the initiate.
     */
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
        } else if (imagesSize > 1 && slideType == TYPE_END_SLIDE) {
            Log.i(TAG, "size > 1 and Type = End Slide");
            currentItem = 0;
        } else if (imagesSize > 1 && slideType == TYPE_INFINITE_SLIDE) {
            Log.i(TAG, "size > 1 and Type = Infinite Slide");
            currentItem = 1;
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

            if (effectStyle == EFFECT_FADE_SLIDE) {
                enableFadeEffect();
            }

            viewPager.setScrollDuration(slideDuration);

            return true;
        }
        return false;
    }

    public Sliderize enableSlideEventListener (OnSlideEventListener listener) {
        onSlideEventListener = listener;
        return this;
    }

    /**
     * Enable Fade Effect
     * Transition between slides
     */
    private void enableFadeEffect() {
        if (viewPager != null) {
            viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        if (position <= -1.0F || position >= 1.0F) {
                            page.setTranslationX(page.getWidth() * position);
                            page.setAlpha(0.0F);
                        } else if (position == 0.0F) {
                            page.setTranslationX(page.getWidth() * position);
                            page.setAlpha(1.0F);
                        } else {
                            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                            page.setTranslationX(page.getWidth() * -position);
                            page.setAlpha(1.0F - Math.abs(position));
                        }
                    }
                }
            });
        }
    }

    /**
     * Time that takes between each slide to animate to the next one.
     *
     * @param duration int: milliseconds : Default 500 (half a second)
     * @return itself.
     */
    public Sliderize changeTransitionTime(int duration) {
        this.slideDuration = duration;
        return this;
    }

    /**
     * create viewpager and add it inside the sliderView
     */
    private void createSlider() {
        viewPager = new SliderizeViewPager(ctx);
        sliderView.addView(viewPager);
        viewPager.setOffscreenPageLimit(loadedPageLimit);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            private float pointX;
            private float pointY;
            private int tolerance = 1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pointX = event.getX();
                    pointY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance < event.getX();
                    boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance < event.getY();
                    if(sameX && sameY){
                        onSlideEventListener.onSlideClicked(getCurrentSlide());
                        return true;
                    }
                }
                if (enableTimer) {
                    hanlder.removeCallbacks(updater);
                    hanlder.postDelayed(updater, timerDuration);
                }
                return false;
            }
        });
    }

    /**
     * Add Dot system to the slider so it will display at which slide we currently are
     */
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
            if (enableTimer) {
                startAutoPlay();
            }
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
                        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == data.size() - 1)
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

                    if (onSlideEventListener != null) {
                        onSlideEventListener.onSlideCompleted(getCurrentSlide());
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

        if (enableTimer) {
            startAutoPlay();
        }

    }

    /**
     * Enable End to End Type
     * Default Functionality of viewpager
     */
    private float test = 1.0f;
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

                    if (onSlideEventListener != null)
                        onSlideEventListener.onSlideCompleted(currentItem);

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
            if (enableTimer) {
                startAutoPlay();
            }

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

                    if (onSlideEventListener != null) {
                            onSlideEventListener.onSlideCompleted(getCurrentSlide());
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    /**
     * Get Current Slide
     * @return
     */
    private int getCurrentSlide () {
        if (slideType == TYPE_END_SLIDE) {
            return currentItem;
        } else if (slideType == TYPE_INFINITE_SLIDE || slideType == TYPE_RESTART_SLIDE) {
            if (currentItem - 1 < 0) {
                return currentItem;
            } else {
                return currentItem - 1;
            }
        } else {
            return 0;
        }
    }

    private void startAutoPlay() {
        Log.i("Timer", "enabled");
        if (hanlder == null)
            hanlder = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                if (currentItem % 2 == 0)
                    Log.i("Timer", "...updater running...");
                if (slideType == TYPE_RESTART_SLIDE) {
                    if (currentItem >= 1 && currentItem <= data.size() - 2) {
                        viewPager.setCurrentItem(currentItem++, true);
                    } else if (currentItem > data.size() - 2) {
                        currentItem = 1;
                        viewPager.setCurrentItem(currentItem, true);
                    } else if (currentItem < 1) {
                        currentItem = data.size() - 2;
                        viewPager.setCurrentItem(currentItem, true);
                    }
                } else if (slideType == TYPE_INFINITE_SLIDE) {
                    if (currentItem >= 0 && currentItem <= data.size() - 1) {
                        viewPager.setCurrentItem(currentItem++, true);
                    }
                } else if (slideType == TYPE_END_SLIDE) {
                    if (reverseEndSlider) {
                        if (currentItem >= 0 && currentItem <= data.size() - 1) {
                            viewPager.setCurrentItem(currentItem--, true);
                            if (currentItem == 0) {
                                reverseEndSlider = false;
                            }
                        }
                    } else {
                        if (currentItem >= 0 && currentItem <= data.size() - 1) {
                            viewPager.setCurrentItem(currentItem++, true);
                            if (currentItem == data.size() - 1) {
                                reverseEndSlider = true;
                            }
                        }
                    }

                }
                hanlder.postDelayed(this, timerDuration);
            }
        };

        hanlder.postDelayed(updater, timerDuration);
    }

    /**
     * Enable or disable slider
     *
     * @param active boolean: default false
     * @return itself.
     */
    public Sliderize setTimerActive(Boolean active) {
        enableTimer = active;
        return this;
    }

    /**
     * Change the default transition of how slides slide.
     *
     * @param effectStyle int: default Sliderize.EFFECT_FADE_SLIDE
     * @return itself.
     */
    public Sliderize changeEffectSlide(int effectStyle) {
        this.effectStyle = effectStyle;
        return this;
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
        if (hanlder != null && updater != null)
            hanlder.removeCallbacks(updater);
        updater = null;
        hanlder = null;
    }

}
