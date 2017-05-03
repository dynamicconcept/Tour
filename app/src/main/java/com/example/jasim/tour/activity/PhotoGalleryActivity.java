package com.example.jasim.tour.activity;

import android.Manifest;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.jasim.tour.R;
import com.example.jasim.tour.adapter.GalleryImageAdapter;

import java.util.ArrayList;


public class PhotoGalleryActivity extends Activity {

    ArrayList<String> imagePath;
    GalleryImageAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            return;
        } else {
            showActivity();
        }
    }

    private void showActivity() {
        setContentView(R.layout.custom_gallery);
        GridView grdImages = (GridView) findViewById(R.id.grdImages);
        Button btnSelect = (Button) findViewById(R.id.btnSelect);
        imagePath = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        CursorLoader loader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, orderBy);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imagePath.add(cursor.getString(dataColumnIndex));
                cursor.moveToNext();
            }
        }
        if (cursor != null)
            cursor.close();
        adapter = new GalleryImageAdapter(this, imagePath);
        grdImages.setAdapter(adapter);

        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (adapter.getSelectedImage().isEmpty()) {
                    Toast.makeText(PhotoGalleryActivity.this,
                            "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent();
                    i.putExtra("data", adapter.getSelectedImage());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showActivity();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("You must give permission").setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}