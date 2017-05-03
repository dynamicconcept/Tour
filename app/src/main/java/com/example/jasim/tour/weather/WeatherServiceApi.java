package com.example.jasim.tour.weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import com.example.jasim.tour.weather.Model.Mode;


public interface WeatherServiceApi {
    @GET() Call<Mode> getWeekData(@Url String url);
    @GET Call<ResponseBody> getImage(@Url String url);
}
