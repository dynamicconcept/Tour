package com.example.jasim.tour.mymap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.jasim.tour.R;
import com.example.jasim.tour.mymap.model.placeDetails.MapDetails;
import com.example.jasim.tour.mymap.model.placeDetails.Result;

public class MapLocationDetailsActivity extends Activity {
    WebView mWvPlaceDetails;
    ServiceApi serviceApi;
    Result mapDetails;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_details);
        mWvPlaceDetails = (WebView) findViewById(R.id.wv_place_details);
        mWvPlaceDetails.getSettings().setUseWideViewPort(false);

        // Getting place reference from the map
        String placeId = getIntent().getStringExtra("placeId");
        networkInitial();
        StringBuilder sb = new StringBuilder("details/json?");
        sb.append("placeid=" + placeId);
        sb.append("&key=AIzaSyDC9RhGiqfA3JrgmZvFUptC2FgcL17kUek  ");
        downloadPlace(sb.toString());
    }

    private void networkInitial() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);
    }

    private void downloadPlace(String url) {
        Call<MapDetails> response = serviceApi.getPlaceDetails(url);
        response.enqueue(new Callback<MapDetails>() {
            @Override
            public void onResponse(Call<MapDetails> call, Response<MapDetails> mapResponse) {
                mapDetails = mapResponse.body().getResult();

                String name, icon, vicinity, formatted_address, formatted_phone, website,
                        international_phone_number;
                double rating, lat, lng;
                name = mapDetails.getName();
                icon = mapDetails.getIcon();
                vicinity = mapDetails.getVicinity();
                lat = mapDetails.getGeometry().getLocation().getLat();
                lng = mapDetails.getGeometry().getLocation().getLng();
                formatted_address = mapDetails.getFormattedAddress();
                formatted_phone = mapDetails.getFormattedPhoneNumber();
                website = mapDetails.getWebsite();
                if (mapDetails.getRating() != null)
                    rating = mapDetails.getRating();
                else rating=0;
                if (mapDetails.getInternationalPhoneNumber()!=null)
                international_phone_number = mapDetails.
                        getInternationalPhoneNumber();
                else international_phone_number="";

                String mimeType = "text/html";
                String encoding = "utf-8";

                String data = "<html>" +
                        "<body><img style='float:left' src=" + icon + " /><h1><center>" + name + "</center></h1>" +
                        "<br style='clear:both' />" +
                        "<hr  />" +
                        "<p>Vicinity : " + vicinity + "</p>" +
                        "<p>Location : " + lat + "," + lng + "</p>" +
                        "<p>Address : " + formatted_address + "</p>" +
                        "<p>Phone : " + formatted_phone + "</p>" +
                        "<p>Website : " + website + "</p>" +
                        "<p>Rating : " + rating + "</p>" +
                        "<p>International Phone  : " + international_phone_number + "</p>" +
//                        "<p>URL  : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";

                // Setting the data in WebView
                mWvPlaceDetails.loadDataWithBaseURL("", data, mimeType, encoding, "");

            }

            @Override
            public void onFailure(Call<MapDetails> call, Throwable t) {

            }
        });
        Log.e("mGoogleMap", "url: " + response.request().url());
    }
}
