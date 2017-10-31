package com.androidstudy.andelatrackchallenge.network;

import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by anonymous on 10/10/17.
 */

public class ApiClient {

    private static Retrofit retrofit;
    private static OkHttpClient client;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
                    .client(getOkClient())
                    .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
                    .build();
        }
        return retrofit;
    }

    public static com.androidstudy.andelatrackchallenge.network.Api getApi() {
        return getClient().create(com.androidstudy.andelatrackchallenge.network.Api.class);
    }

    public static Moshi getMoshi() {
        return new Moshi.Builder()
                .build();
    }

    public static OkHttpClient getOkClient() {
        if (client == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        }
        return client;
    }
}
