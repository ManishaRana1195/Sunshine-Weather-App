package com.example.manisharana.sunshine.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.widget.ImageView;

import com.example.manisharana.sunshine.Activities.SatelliteImageActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SatelliteImageAsyncTask extends AsyncTask<String, String, Bitmap> {

    private final ImageView imageView;
    private Bitmap bitmapFact;
    private ProgressDialog progressDialog;

    public SatelliteImageAsyncTask(Context context, ImageView imageView) {
        this.imageView = imageView;
        progressDialog = new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Dowloading");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... args) {
        try {
            Document doc  = Jsoup.connect(args[0]).get();
            Element image = doc.select("#main-image-content").first();
            URL url = new URL("http://en.sat24.com"+image.attr("src"));
            InputStream is = new BufferedInputStream(url.openStream());
            bitmapFact = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapFact;
    }


    @Override
    protected void onPostExecute(Bitmap s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        imageView.setImageBitmap(s);
    }

}
