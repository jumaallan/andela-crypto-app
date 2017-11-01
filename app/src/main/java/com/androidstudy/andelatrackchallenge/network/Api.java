package com.androidstudy.andelatrackchallenge.network;

import com.androidstudy.andelatrackchallenge.models.Exchange;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by anonymous on 10/10/17.
 */

public interface Api {
    /**
     * An Interface with our Retrofit Endpoints
     */

    String FROM_SYMBOL = "fsym";
    String TO_SYMBOLS = "tsyms";

    @GET("price")
    Single<Response<Exchange>> getPrice(@Query("fsym") String from, @Query("tsyms") String to);
}