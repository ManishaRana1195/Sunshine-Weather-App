package com.example.manisharana.sunshine.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.manisharana.sunshine.Fragments.ForecastFragment;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.Utility;

public class MainActivity extends AppCompatActivity {

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.weekly_forecast_fragment);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            mLocation = location;
        }
    }
}
