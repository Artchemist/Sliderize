package com.artchemylab.sliderize;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 * Created by Artchemist on 8/10/2016.
 */
public class SliderizeAdapter extends PagerAdapter {

    private Context ctx;
    private List<String> data;

    public SliderizeAdapter (Context ctx, List<String> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.sliderize_default_view, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.image_view_default_slide);

        /**
         * if data contains "first" and "last" means there are placeholders for the repeat mode loop effect
         * No need for picasso to make a request if that's the case.
         * for end to end type and infinite type we need picasso to run run for all items inside data
         */
        if (!data.get(position).equals("first") && !data.get(position).equals("last"))
            Picasso.with(ctx).load(data.get(position)).into(image);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
