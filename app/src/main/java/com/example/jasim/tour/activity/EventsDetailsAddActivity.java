package com.example.jasim.tour.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.jasim.tour.R;
import com.example.jasim.tour.adapter.ImageAdapter;
import com.example.jasim.tour.database.EventDetailsDbSource;
import com.example.jasim.tour.databinding.ActivityEventsDetailsAddBinding;
import com.example.jasim.tour.model.EventDetails;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventsDetailsAddActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityEventsDetailsAddBinding binding;
    SharedPreferences sharedPreferences;
    boolean showFullScreen;
    private File TourMateImageFolder;
    String picture = "";
    Uri output;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_events_details_add);
        setSupportActionBar(binding.toolbar.toolBar);
        getSupportActionBar().setTitle("Moments Add");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("appsData", MODE_PRIVATE);
        showFullScreen = sharedPreferences.getBoolean("showFullScreen", false);
        if (showFullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String tourMateFolderName = "TourMate Images";
        TourMateImageFolder = new File(storageDirectory, tourMateFolderName);
        boolean success = true;
        if (!TourMateImageFolder.exists()) {
            success = TourMateImageFolder.mkdirs();
        }
        if (!success) {
            AlertDialog.Builder dialog;
            //checking android version for lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                dialog = new AlertDialog.Builder(this);
            }
            dialog.setMessage("External Sd Card Unreadable. Try Again").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            dialog.show();
        }
        binding.cameraBtn.setOnClickListener(this);
        binding.galleryBtn.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);
        binding.submitBtn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.cameraBtn:
                if (!picture.equals("")) {
                    AlertDialog.Builder dialog;
                    //checking android version for lollipop
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        dialog = new AlertDialog.Builder(this);
                    }
                    dialog.setMessage("Are you sure you want to override Current Image? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            picture="";
                            try {
                                openCamera();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                }
                if (ActivityCompat.checkSelfPermission(EventsDetailsAddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(EventsDetailsAddActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                    return;
                } else {
                    try {
                        openCamera();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.galleryBtn:
                if (ActivityCompat.checkSelfPermission(EventsDetailsAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    picture="";
                    ActivityCompat.requestPermissions(EventsDetailsAddActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else if (!picture.equals("")) {
                AlertDialog.Builder dialog;
                //checking android version for lollipop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    dialog = new AlertDialog.Builder(this);
                }
                dialog.setMessage("Are you sure you want to override Current Image? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        picture = "";
                        openGallery();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
            else openGallery();
                break;

            case R.id.cancelBtn:
                finish();
                break;

            case R.id.submitBtn:
                String description = binding.description.getText().toString().trim();
                String stringUsed = binding.used.getText().toString().trim();
                if (picture.equals(""))
                    Toast.makeText(EventsDetailsAddActivity.this,
                            "You must add picture", Toast.LENGTH_SHORT).show();
                else if (description.equals("")) {
                    binding.description.setError("Description Need");
                    binding.description.requestFocus();
                } else {

                    long eventId = getIntent().getLongExtra("eventId", 0);
                    if (eventId != 0) {
                        int used = 0;
                        if (!stringUsed.equals(""))
                            used = Integer.valueOf(stringUsed);
                        String addingTime = new SimpleDateFormat("dd/MM/yyyy hh:mma",
                                Locale.getDefault())
                                .format(new Date());
                        EventDetails eventDetails = new EventDetails(eventId, description,
                                picture, addingTime, used);

                        EventDetailsDbSource source = new EventDetailsDbSource(this);
                        eventDetails = source.addEventDetails(eventDetails);
                        if (eventDetails != null)
                            setResult(RESULT_OK, new Intent()
                                    .putExtra("newEventDetails", eventDetails));
                    }
                    finish();
                }
                break;

            default:
                break;
        }
    }

    public void openCamera() throws IOException {
        Intent openCamera = new Intent();
        openCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = TourMateImageFolder + "/TourMate_" + timeStamp + ".jpg";
        File image = new File(imageFileName);
        output = Uri.fromFile(image);
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, output);
        time = System.currentTimeMillis();
        startActivityForResult(openCamera, 0);
    }

    public void openGallery() {
        Intent openGallery = new Intent(this, PhotoGalleryActivity.class);
        startActivityForResult(openGallery, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                openCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else Toast.makeText(EventsDetailsAddActivity.this,
                "You Rejects The Permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 && resultCode == RESULT_OK) {
            String[] projection = {MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    BaseColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_ADDED};
            final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
            final String selection = MediaStore.Images.Media.DATE_TAKEN + " > " + time;
            Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            CursorLoader loader = new CursorLoader(this, u,
                    projection, selection, null, imageOrderBy);
            Cursor cursor = loader.loadInBackground();
            if (null != cursor && cursor.moveToFirst()) {
                ContentResolver cr = this.getContentResolver();
                cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        BaseColumns._ID + "=" + cursor.getString(3), null);
            }
            //showing image in image view
            picture = output.getPath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(picture, options);
            binding.imageView.setImageBitmap(bitmap);
            binding.imageView.setVisibility(View.VISIBLE);
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> imagesPathList = data.getStringArrayListExtra("data");
          String[] imagesPath = data.getStringExtra("data").split("\\|");
            ImageAdapter imageAdapter = new ImageAdapter(this, imagesPathList);
            for (int i = 0; i < imagesPathList.size(); i++) {
                picture += imagesPathList.get(i) + "@@";
            }
            binding.gridView.setAdapter(imageAdapter);
            binding.gridView.setVisibility(View.VISIBLE);
        } else
            Toast.makeText(EventsDetailsAddActivity.this, "Image add cancel",
                    Toast.LENGTH_SHORT).show();
    }

}
