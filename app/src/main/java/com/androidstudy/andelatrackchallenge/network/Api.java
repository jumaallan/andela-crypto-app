package com.androidstudy.andelatrackchallenge.network;

import com.androidstudy.andelatrackchallenge.models.Exchange;
import com.androidstudy.andelatrackchallenge.models.History;

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

    //Ex. https://min-api.cryptocompare.com/data/histoday?fsym=USD&tsym=BTC&limit=5&aggregate=20&allData=true
    @GET("histoday?limit=5&aggregate=20&allData=true")
    Single<Response<History>> getDayHistory(@Query("fsym") String from, @Query("tsym") String to);

    // Ex. https://min-api.cryptocompare.com/data/histohour?fsym=USD&tsym=BTC&limit=24&aggregate=1
    @GET("histohour?limit=24&aggregate=1")
    Single<Response<History>> getHourHistory(@Query("fsym") String from, @Query("tsym") String to);
}