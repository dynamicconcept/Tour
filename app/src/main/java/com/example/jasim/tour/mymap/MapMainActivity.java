package com.example.jasim.tour.mymap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.jasim.tour.R;
import com.example.jasim.tour.databinding.MapActivityMainBinding;
import com.example.jasim.tour.mymap.model.place.Map;


public class MapMainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    MapActivityMainBinding binding;
    GoogleMap mGoogleMap;
    SupportMapFragment fragment;
    GoogleApiClient googleApiClient;
    UiSettings uiSettings;
    ServiceApi serviceApi;
    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;
    Map map;
    double latitude = 0;
    double longitude = 0;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.map_activity_main);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        // Array of place types
        mPlaceType = getResources().getStringArray(R.array.place_type);
        // Array of place type names
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

        // Setting adapter on Spinner to set place types
        binding.placeType.setAdapter(adapter);

        fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        // Setting click event lister for the find button
        binding.findBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                networkInitial();
                int selectedPosition = binding.placeType.getSelectedItemPosition();
                String type = mPlaceType[selectedPosition];
                StringBuilder sb = new StringBuilder("nearbysearch/json?");
                sb.append("location=" + latitude + "," + longitude);
                sb.append("&radius=5000");
                sb.append("&types=" + type);
                sb.append("&sensor=true");
                sb.append("&key=AIzaSyDC9RhGiqfA3JrgmZvFUptC2FgcL17kUek ");
                downloadPlace(sb.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected())
        googleApiClient.disconnect();
    }

    private void networkInitial() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);
    }

    private void downloadPlace(String url) {
        Call<Map> response = serviceApi.getPlace(url);
        response.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> mapResponse) {
                map = mapResponse.body();
                if (map.getResults() != null) {
                    Log.e("mGoogleMap", "data not null, status " + map.getStatus());
                    // Creating a new non-ui thread task to add places in Map
                    new PlacesTask().execute();
                } else Log.e("mGoogleMap", "data null, status " + map.getStatus());
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });
        Log.e("mGoogleMap", "url: " + response.request().url());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        CircleOptions circle= new CircleOptions();
//        circle.center()
//        mGoogleMap.addCircle(circle);

        mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                //Starting the Place Details Activity
                startActivity(new Intent(MapMainActivity.this,
                        MapLocationDetailsActivity.class)
                .putExtra("placeId", placeId));
            }
        });

        uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mGoogleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(latitude, longitude);

        Log.e("mGoogleMap", "lat " + latitude + " lon " + longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<Void, Void, Nullable> {

        // Invoked by execute() method of this object
        @Override
        protected Nullable doInBackground(Void... result) {
            Log.e("mGoogleMap", "background call");
            return null;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(Nullable nullable) {
            Log.e("mGoogleMap", "on working with data call");
            mGoogleMap.clear();

            List<com.example.jasim.tour.mymap.model.place.Result> results = map.getResults();
            for (int i = 0; i < results.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting latitude and long of the place
                double lat = results.get(i).getGeometry().getLocation().getLat();
                double lng = results.get(i).getGeometry().getLocation().getLng();

                // Getting id and place name
                String id = results.get(i).getPlaceId();
                String name = results.get(i).getName();

                // Getting vicinity
                String vicinity = results.get(i).getVicinity();

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name);

                // Placing a marker on the touched position
                mGoogleMap.addMarker(markerOptions);

                // Linking Marker id and place reference
                placeId = id;
                Log.e("mGoogleMap", "doing data ");
            }

        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e("Location", "On connected");
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(30000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
        Log.e("Location", "On connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Location", "On connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        fragment.getMapAsync(this);
        googleApiClient.disconnect();
        Log.e("Location", "lat " + latitude + " lon " + longitude);
    }
}