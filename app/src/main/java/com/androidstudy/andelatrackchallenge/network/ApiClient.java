package com.androidstudy.andelatrackchallenge.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anonymous on 10/10/17.
 */

public class ApiClient {
    /**
     * This class will help us create a re-usable Retrofit Client,
     * to avoid repeating our code!
     */

    private static retrofit2.Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
