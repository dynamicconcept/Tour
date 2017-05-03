package com.example.jasim.tour.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


import com.example.jasim.tour.R;
import com.example.jasim.tour.weather.Model.Mode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeeklyInfo extends AppCompatActivity {
    com.example.jasim.tour.databinding.ActivityWeeklyInfoBinding binding;
    PagerAdapter pagerAdapter;
    boolean showFullScreen;
    WeatherServiceApi weatherServiceApi;
    Mode mode;
    double latitude = 0;
    double longitude = 0;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitude = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("lon", 0);
        //getting values when device rotated
        if (savedInstanceState != null) {
            latitude = savedInstanceState.getDouble("lat");
            longitude = savedInstanceState.getDouble("lon");
        }
        if (!isOnline()) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("You don't have any active data connection!!!").setCancelable(false).setPositiveButton("Enable Data", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                    dialogInterface.dismiss();
                }
            }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showActivity();
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        } else showActivity();
    }

    public void showActivity() {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_weekly_info);
            //for getting icon for image view
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            networkInitial();
            if (latitude != 0 && longitude != 0) {
                String query = String.format("lat=%f&lon=%f", latitude, longitude);
                getWeekData(query);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WeeklyInfo.this);
                dialog.setMessage("Your current location could not found!!!" +
                        "\n\tPlease Try again or search location")
                        .setCancelable(false).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                dialog.show();
            }

            //setting toolbar bar
            setSupportActionBar(binding.toolbar.toolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Full Week Weather");


            //getting settings from SharedPreferences
            showFullScreen = getIntent().getBooleanExtra("screen", false);
            if (showFullScreen) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            fragmentManager = getSupportFragmentManager();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble("lat", latitude);
        savedInstanceState.putDouble("lon", longitude);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        //setting search in menu bar
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                //making main view hide so that old info don't show
                binding.mainView.setVisibility(View.GONE);
                String query = "q=" + q;
                getWeekData(query);
                //exiting search bar after submit query
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getWeekData(String query) {
        String url = String.format("data/2.5/forecast/daily?%s&cnt=7&APPID=792d86c8923945df0599d9fb68e3333e", query);
        Call<Mode> response = weatherServiceApi.getWeekData(url);
        response.enqueue(new Callback<Mode>() {
            @Override
            public void onResponse(Call<Mode> call, Response<Mode> response) {
                Log.e("weather", "downloading data") ;
                mode = response.body();
                if (mode.getCity() != null) {
                    binding.mainView.setVisibility(View.VISIBLE);
                    pagerAdapter = new PagerAdapter(fragmentManager, WeeklyInfo.this, mode);
                    binding.pagerView.setAdapter(pagerAdapter);
                    binding.slideLayOut.setupWithViewPager(binding.pagerView);
                    for (int i = 0; i < mode.getList().size(); i++) {
                        binding.slideLayOut.getTabAt(i).setText(pagerAdapter.getPageTitle(i));
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WeeklyInfo.this);
                    dialog.setMessage("Your input City is Wrong!!!\n\tPlease Try again").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<Mode> call, Throwable t) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WeeklyInfo.this);
                dialog.setMessage("There is problem in server").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                dialog.show();
            }
        });
        Log.e("WeeklyWeather", "url: " + response.request().url());
    }

    private void networkInitial() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherServiceApi = retrofit.create(WeatherServiceApi.class);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
          recreate();
        }
    }
}
