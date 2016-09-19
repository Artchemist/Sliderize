package com.approveit.demolib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.artchemylab.sliderize.Sliderize;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Sliderize sliderize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout sliderView = (RelativeLayout) findViewById(R.id.sliderView);

        /**
         * Some Placeholder data for the demo
         */
        List<String> data = new ArrayList<>();
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/house_1_desert_village__remake__by_johnboul-d6wmht1.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/LoveBirds_%C2%A9Johnboulmetis.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/WinterNight_Post.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/argonath.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/dwmatio_01.jpg");
        data.add("http://www.johnboulmetis.com/wp-content/uploads/2016/04/digital_painting__hourse_archer_by_johnboul-d8wftgs.jpg");


        /**
         * You only need these two lines to run the slider effect
         * new Sliderize(context).with(your array list with images).to(your relative layout);
         * sliderize.initiate();
         * and you got the default slider
         */
        sliderize = new Sliderize(this).with(data).to(sliderView).changeLoadedPageLimit(10).setSlideType(Sliderize.TYPE_END_SLIDE).setTimerDuration(1500).setTimerActive(false);
        sliderize.initiate();
    }

    /**
     * You only need these for the timer (not yet implemented)
     * onDestroy clears everything out.
     */
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
