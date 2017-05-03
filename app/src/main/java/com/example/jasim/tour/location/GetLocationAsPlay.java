package com.example.jasim.tour.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class GetLocationAsPlay implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Context context;
    double latitude;
    double longitude;
    GoogleApiClient googleApiClient;
//    MainActivity mainActivity;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public GetLocationAsPlay(Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
//        apiConnect();
//        Activity ac = MainActivity.activity;
//        mainActivity= (MainActivity) ac;

    }

    public void apiConnect() {
        if (googleApiClient != null) {

        }
        Log.e("Location", "On ApiConnected");
    }

    public void apiDisconnect() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        Log.e("Location", "On Apdisconnected");
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e("Location", "On connected");
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        mainActivity.latitude=location.getLatitude();
//        mainActivity.longitude=location.getLongitude();
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        Log.e("Location", "lat" + latitude + " lon" + longitude);
//        apiDisconnect();
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(20000);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        Log.e("Location", "On Location Change");
        Log.e("Location", "lat"+latitude+" lon"+longitude);
//        apiDisconnect();
    }
}
