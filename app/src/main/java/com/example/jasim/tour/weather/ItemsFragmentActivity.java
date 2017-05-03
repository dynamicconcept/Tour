package com.example.jasim.tour.weather;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jasim.tour.R;
import com.example.jasim.tour.weather.Helpers.TemperatureFormatter;
import com.example.jasim.tour.weather.Model.Mode;

import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ItemsFragmentActivity extends Fragment {
    com.example.jasim.tour.databinding.WeeklyDetailsBinding binding;
    List<com.example.jasim.tour.weather.Model.List> weekList;
    WeatherServiceApi weatherServiceApi;
    Mode mode;
    Bitmap bitmap;
    int position;

    public ItemsFragmentActivity getInstance(int position, Mode mode) {
        ItemsFragmentActivity myFragment = new ItemsFragmentActivity();
        this.mode = mode;
        this.weekList = mode.getList();
        this.position = position;
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.weekly_details, parent, false);
        binding.cityName.setText(mode.getCity().getName() + ", " + mode.getCity().getCountry());


        networkInitial();
        String code = weekList.get(position).getWeather().get(0).getIcon();
        downloadImage("img/w/" + code + ".png");

        binding.temperature.setText(TemperatureFormatter.format(weekList.get(position).getTemp().getDay()));
        binding.weatherDetails.setText(weekList.get(position).getWeather().get(0).getMain().toUpperCase() + ", " + weekList.get(position).getWeather().get(0).getDescription().toUpperCase());
        binding.upDatesTime.setText("Info Last Updates at: N/A ");
        binding.windDetails.setText(weekList.get(position).getSpeed().toString() + "m/s");
        binding.cloudDetails.setText(weekList.get(position).getWeather().get(0).getDescription());
        binding.pressureDetails.setText(weekList.get(position).getPressure().toString());
        binding.humidityDetails.setText(weekList.get(position).getHumidity().toString() + "%");
        if (weekList.get(position).getRain() != null)
            binding.rain.setText(weekList.get(position).getRain().toString());
        binding.clouds.setText(weekList.get(position).getClouds().toString());
        binding.maxTemp.setText(TemperatureFormatter.format(weekList.get(position).getTemp().getMax()));
        binding.minTemp.setText(TemperatureFormatter.format(weekList.get(position).getTemp().getMin()));
        return binding.getRoot();
    }

    private void networkInitial() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherServiceApi = retrofit.create(WeatherServiceApi.class);
    }

    private void downloadImage(String url){

        Call<ResponseBody> call = weatherServiceApi.getImage(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                     bitmap = BitmapFactory.
                            decodeStream(response.body().byteStream());
                     new IconLoad().execute();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }




    private class IconLoad extends AsyncTask<Void, Void, Nullable> {
        WeakReference<ImageView> imageView;

        @Override
        protected Nullable doInBackground(Void... url) {
             return null;
        }

        @Override
        protected void onPostExecute(Nullable nullable) {
            if (bitmap != null) {
                binding.weatherIcon.setImageBitmap(bitmap);
            }
        }
    }
}

