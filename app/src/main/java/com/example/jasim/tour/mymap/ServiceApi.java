package com.example.jasim.tour.mymap;

import com.example.jasim.tour.mymap.model.place.Map;
import com.example.jasim.tour.mymap.model.placeDetails.MapDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import com.example.jasim.tour.mymap.model.place.Map;
import com.example.jasim.tour.mymap.model.placeDetails.MapDetails;


public interface ServiceApi {
    @GET()
    Call<Map> getPlace(@Url String url);
    @GET()
    Call<MapDetails> getPlaceDetails(@Url String url);
}
