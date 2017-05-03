package com.example.jasim.tour.adapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import com.example.jasim.tour.R;
import com.example.jasim.tour.activity.MainActivity;


public class SetBitmap extends AsyncTask<String, Void, Bitmap>{
    WeakReference<ImageView> imageView;
    Context context;
    MainActivity activity;

    public SetBitmap(ImageView image, Context context) {
        imageView = new WeakReference<>(image);
        this.context = context;
        Activity ac = MainActivity.activity;
        activity= (MainActivity) ac;
    }

    @Override
    protected Bitmap doInBackground(String... files) {
        Bitmap bitmap = activity.mBitmapCache.get(files[0]);
        if (bitmap != null)
            return bitmap;
        else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(files[0], options);
            if (bitmap != null)
                return bitmap;
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.no_image);
            activity.mBitmapCache.put(files[0], bitmap);
            return bitmap;
        }

    }

    @Override
    protected void onPostExecute(Bitmap image) {
        if (image != null && imageView != null) {
            ImageView imageV = imageView.get();
            if (imageV != null)
                imageV.setImageBitmap(image);
        }
    }
}
