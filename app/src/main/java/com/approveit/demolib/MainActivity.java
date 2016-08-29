package com.approveit.demolib;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.artchemylab.sliderize.Sliderize;
import com.artchemylab.sliderize.SliderizeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Sliderize sliderize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout sliderView = (RelativeLayout) findViewById(R.id.sliderView);

        List<String> data = new ArrayList<>();
        data.add("http://lorempixel.com/400/200");
//        data.add("http://lorempixel.com/400/200");
//        data.add("http://lorempixel.com/400/200");
//        data.add("http://lorempixel.com/400/200");
//        data.add("http://lorempixel.com/400/200");
//        data.add("http://lorempixel.com/400/200");

        sliderize = new Sliderize(this).with(data).to(sliderView);
        sliderize.initiate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderize.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderize.onDestroy();
    }


}
