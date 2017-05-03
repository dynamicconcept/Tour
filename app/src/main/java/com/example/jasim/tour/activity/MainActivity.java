package com.example.jasim.tour.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.jasim.tour.R;
import com.example.jasim.tour.adapter.EventsAdapter;
import com.example.jasim.tour.database.EventsDbSource;
import com.example.jasim.tour.database.UserDbSource;
import com.example.jasim.tour.databinding.ActivityMainBinding;
import com.example.jasim.tour.databinding.PopUpLayoutBinding;
import com.example.jasim.tour.location.GetLocation;
import com.example.jasim.tour.location.GetLocationAsPlay;
import com.example.jasim.tour.model.Events;
import com.example.jasim.tour.model.User;
import com.example.jasim.tour.mymap.MapMainActivity;
import com.example.jasim.tour.weather.WeeklyInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public Map<String, Bitmap> mBitmapCache = new HashMap<>();
    Dialog customDialog;
    PopUpLayoutBinding dialogBinding;
    ActivityMainBinding binding;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean hideToolbar;
    boolean showFullScreen;
    public long userId;
    ArrayList<Events> allEvents;
    EventsDbSource eventsDbSource;
    EventsAdapter eventsAdapter;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //getting settings from SharedPreferences
        sharedPreferences = getSharedPreferences("appsData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        hideToolbar = sharedPreferences.getBoolean("hideToolbar", false);
        showFullScreen = sharedPreferences.getBoolean("showFullScreen", false);
        userId = sharedPreferences.getLong("userId", 0);
//        try {
//            ViewConfiguration config = ViewConfiguration.get(this);
//            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//            if (menuKeyField != null) {
//                menuKeyField.setAccessible(true);
//                menuKeyField.setBoolean(config, false);
//            }
//        } catch (Exception ignored) {
//        }

        //checking user status login or not
        if (userId == 0) { //user is not logged in
            //initialing for custom dialog view
            dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.pop_up_layout, null, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                customDialog = new Dialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                customDialog = new Dialog(this);
            }
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setContentView(dialogBinding.getRoot());
            customDialog.setCancelable(false);
            customDialog.show();
            dialogBinding.dialogCancel.setOnClickListener(this);
            dialogBinding.dialogLogin.setOnClickListener(this);
            dialogBinding.signUp.setOnClickListener(this);
            dialogBinding.dialogForgetBtn.setOnClickListener(this);

        } else //user previously logged in
            mainView();

    }

    //here our all main activity code
    public void mainView() {
        //initialing main activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar.toolBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);
        invalidateOptionsMenu();  //for change setting depends on user choice
//        View v = findViewById(R.id.);
//        setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

        //setting navigation bar
//        int width = getResources().getDisplayMetrics().widthPixels/2;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.toolbar.toolBar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);

        //configure settings
        if (hideToolbar) getSupportActionBar().hide();
        if (showFullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        binding.eventAddBtn.setOnClickListener(this);
        eventsDbSource = new EventsDbSource(this);
        allEvents = eventsDbSource.getEvents(userId);
        if (!allEvents.isEmpty()) eventInitial();

        binding.eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Events event = eventsAdapter.getItem(i);
                startActivity(new Intent(MainActivity.this, EventDetailsActivity.class).putExtra("event", event));
            }
        });


    }

    public boolean PlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public void eventInitial() {
        binding.noEventText.setVisibility(View.GONE);
        binding.eventListView.setVisibility(View.VISIBLE);
        eventsAdapter = new EventsAdapter(this, allEvents);
        binding.eventListView.setAdapter(eventsAdapter);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onDestroy() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        super.onDestroy();
    }

    //creating custom menu tittle defend on user settings
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (userId != 0)
            menu.findItem(R.id.signOutUser).setTitle("Sign Out");
        if (showFullScreen) menu.findItem(R.id.fullScreen).setTitle(R.string.hideFullScreen);
        else menu.findItem(R.id.fullScreen).setTitle(R.string.showFullScreen);
        if (hideToolbar) menu.findItem(R.id.hideToolbar).setTitle(R.string.showToolbar);
        else menu.findItem(R.id.hideToolbar).setTitle(R.string.hideToolbar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOutUser) {
            editor.putLong("userId", 0);
            editor.apply();
            finish();
        }

        if (id == R.id.hideToolbar) {
            if (hideToolbar) {
                hideToolbar = false;
                editor.putBoolean("hideToolbar", false);
                editor.apply();
                if (getSupportActionBar() != null)
                    getSupportActionBar().show();
            } else {
                hideToolbar = true;
                editor.putBoolean("hideToolbar", true);
                editor.apply();
                if (getSupportActionBar() != null)
                    getSupportActionBar().hide();
            }
            return true;
        }
        if (id == R.id.fullScreen) {
            if (showFullScreen) {
                showFullScreen = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                editor.putBoolean("showFullScreen", false);
                editor.apply();
            } else {
                showFullScreen = true;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                editor.putBoolean("showFullScreen", true);
                editor.apply();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.dialogCancel:
                customDialog.dismiss();
                finish();
                break;

            case R.id.signUp:
//                customDialog.dismiss();
//                finish();
                startActivityForResult(new Intent(this, UserSignUpActivity.class), 0);
                break;

            case R.id.dialogLogin:
                String userName = dialogBinding.userName.getText().toString().trim();
                String password = dialogBinding.password.getText().toString().trim();
                if (userName.equals("") || password.length() < 4) {
                    if (password.length() < 4) {
                        dialogBinding.password.setError("Password too short");
                        dialogBinding.password.requestFocus();
                    }
                    if (userName.equals("")) {
                        dialogBinding.userName.setError("No User Name");
                        dialogBinding.userName.requestFocus();
                    }
                } else {
                    UserDbSource userDbSource = new UserDbSource(this);
                    User user = userDbSource.getUser(userName, password);
                    //check name and password in database if match then next step will do
                    if (user != null && user.getUserName().equals("@@")) {
                        dialogBinding.userName.setError("User Name is wrong");
                        dialogBinding.userName.requestFocus();
                        dialogBinding.errorView.setVisibility(View.VISIBLE);
                    } else if (user != null && user.getUserName().equals("")) {
                        dialogBinding.password.setError("Password is wrong");
                        dialogBinding.password.requestFocus();
                        dialogBinding.errorView.setVisibility(View.VISIBLE);
                    } else {
                        customDialog.dismiss();
                        userId = user.getUserID();
                        editor.putLong("userId", userId);
                        editor.commit();
                        mainView();
                    }
                }
                break;

            case R.id.dialogForgetBtn:
//                customDialog.dismiss();
//                finish();
                startActivityForResult(new Intent(this,
                        PasswordRecoveryActivity.class), 0);
                break;

            case R.id.eventAddBtn:
                startActivityForResult(new Intent(this, EventAddActivity.class)
                        .putExtra("userId", userId), 1);
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                allEvents.add((Events) data.getSerializableExtra("newEvent"));
                if (eventsAdapter == null)
                    eventInitial();
                else eventsAdapter.notifyDataSetChanged();
            } else Toast.makeText(MainActivity.this,
                    "You canceled tour add", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 0 && requestCode == RESULT_CANCELED) {
            dialogBinding.userName.setText("");
            dialogBinding.userName.setError(null);
            dialogBinding.password.setText("");
            dialogBinding.password.setError(null);
            dialogBinding.errorView.setVisibility(View.GONE);
            dialogBinding.userName.requestFocus();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
            finish();
        }
//        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id==R.id.nav_camera) {
            boolean permission = true;
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                permission = false;
            if (!permission) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            if (permission) {
                        if (PlayServicesAvailable()) {
                            startActivity(new Intent(this, MapMainActivity.class));
                        }

//                    case R.id.weatherInfo:
//                        startActivity(new Intent(context,
//                               com.example.jasim.tour.map.MainActivity.class)
//                                .putExtra("lat", mainActivity.latitude)
//                                .putExtra("lon", mainActivity.longitude));
//                        break;
//
//                    case R.id.updateProfile:
//                        long userId=mainActivity.userId;
//                        startActivity(new Intent(context, UserSignUpActivity.class)
//                                .putExtra("userId", userId));

                }

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, WeeklyInfo.class)
            .putExtra("screen", showFullScreen));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
