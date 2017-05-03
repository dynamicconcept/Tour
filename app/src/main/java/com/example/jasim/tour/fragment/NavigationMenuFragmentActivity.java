package com.example.jasim.tour.fragment;//package tk.rhidoy.tourmate.fragment;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//
//import tk.rhidoy.tourmate.R;
//import tk.rhidoy.tourmate.activity.MainActivity;
//import tk.rhidoy.tourmate.activity.UserSignUpActivity;
//import tk.rhidoy.tourmate.databinding.ActivityNavigationFragmentBinding;
//import tk.rhidoy.tourmate.mymap.MapMainActivity;
//import tk.rhidoy.tourmate.weather.WeeklyInfo;
//
//public class NavigationMenuFragmentActivity extends Fragment implements View.OnClickListener {
//    ActivityNavigationFragmentBinding binding;
//    Context context;
//    Activity activity;
//    MainActivity mainActivity;
//    @Override
//    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
//        binding = DataBindingUtil.inflate(inflater, R.layout.activity_navigation_fragment, container, false);
//        context = binding.getRoot().getContext();
//        activity = (Activity) context;
//        Activity ac = MainActivity.activity;
//        mainActivity = (MainActivity) ac;
//        binding.nearPlace.setOnClickListener(this);
//        binding.weatherInfo.setOnClickListener(this);
//        binding.updateProfile.setOnClickListener(this);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            Toast.makeText(context, "Permission Granted Successfully",
//                    Toast.LENGTH_SHORT).show();
//        else Toast.makeText(context,
//                "You Rejects The Permission", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onClick(View view) {
//        boolean permission = true;
//        if (ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED)
//            permission = false;
//        if (!permission) {
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    1);
//        }
//        if (permission) {
//            int id = view.getId();
//            switch (id) {
//                case R.id.nearPlace:
//                    if (PlayServicesAvailable()) {
//                        startActivity(new Intent(context,
//                                MapMainActivity.class)
//                                .putExtra("lat", mainActivity.latitude)
//                                .putExtra("lon", mainActivity.longitude));
//                    }
//                    break;
//
//                case R.id.weatherInfo:
//                    startActivity(new Intent(context,
//                            tk.rhidoy.tourmate.map.MainActivity.class)
//                            .putExtra("lat", mainActivity.latitude)
//                            .putExtra("lon", mainActivity.longitude));
//                    break;
//
//                case R.id.updateProfile:
//                    long userId=mainActivity.userId;
//                    startActivity(new Intent(context, UserSignUpActivity.class)
//                            .putExtra("userId", userId));
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    public boolean PlayServicesAvailable() {
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
//        if (status != ConnectionResult.SUCCESS) {
//            if (googleApiAvailability.isUserResolvableError(status)) {
//                googleApiAvailability.getErrorDialog(activity, status, 0).show();
//            }
//            return false;
//        }
//        return true;
//    }
//
//}
