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
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/house_1_desert_village__remake__by_johnboul-d6wmht1.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/LoveBirds_%C2%A9Johnboulmetis.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/WinterNight_Post.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/argonath.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/dwmatio_01.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/digital_painting__hourse_archer_by_johnboul-d8wftgs.jpg");


        sliderize = new Sliderize(this).with(data).to(sliderView).changeLoadedPageLimit(5);
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
