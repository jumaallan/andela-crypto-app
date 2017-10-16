package com.androidstudy.andelatrackchallenge.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by anonymous on 10/10/17.
 */

public interface API {
    /**
     * An Interface with our Retrofit Endpoints
     */
    @GET("data/price/")
    Call<Response> getPrice(@QueryMap Map<String, String> queries);
}
