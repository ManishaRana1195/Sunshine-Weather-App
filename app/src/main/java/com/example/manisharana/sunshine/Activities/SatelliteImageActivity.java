package com.example.manisharana.sunshine.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.manisharana.sunshine.AsyncTasks.SatelliteImageAsyncTask;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.Utility;

public class SatelliteImageActivity extends AppCompatActivity {

    private String url = "http://en.sat24.com/en/";
    private ImageView imageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite_image);
        url += Utility.getSatelliteImagePrefLocation(this);
        imageView = (ImageView)this.findViewById(R.id.imageview_satelitte);
        new SatelliteImageAsyncTask(this,imageView).execute(url);
    }

    private class MyBrowser extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
