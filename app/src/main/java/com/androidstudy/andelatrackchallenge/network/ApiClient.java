package com.androidstudy.andelatrackchallenge.network;

import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;

import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.Exchange;
import com.androidstudy.andelatrackchallenge.models.History;
import com.squareup.moshi.Moshi;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

/**
 * Created by anonymous on 10/10/17.
 */

/**
 * This class will help us create a re-usable Retrofit Client and composing parts,
 * to avoid repeating our code!
 */
public class ApiClient {
    public static final String BTC_ETH = "BTC,ETH";
    public static final String BTC = "BTC";
    public static final String ETH = "ETH";

    private static Api api;
    private static Retrofit retrofit;
    private static OkHttpClient client;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
                    .client(getOkClient())
                    .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Api getApi() {
        if (api == null) {
            api = getClient().create(Api.class);
        }
        return api;
    }

    public static Moshi getMoshi() {
        return new Moshi.Builder()
                .build();
    }

    public static OkHttpClient getOkClient() {
        if (client == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Timber.i(message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        }
        return client;
    }

    public static Single<Country> loadRate(Country oldCountry) {
        long minutesBefore = System.currentTimeMillis() - (10 * 60 * 1000);
        if (oldCountry.refreshedAt > minutesBefore)
            return Single.error(new Throwable("No refresh needed!"));

        return getApi().getPrice(oldCountry.code, BTC_ETH)
                .map(response -> {
                    HttpUrl url = response.raw().request().url();
                    String from = url.queryParameter(Api.FROM_SYMBOL);

                    if (TextUtils.isEmpty(from)) {
                        return null;
                    }

                    Exchange exchange = response.body();
                    if (exchange == null) {
                        return null;
                    }

                    if (ObjectsCompat.equals(oldCountry.code, from)) {
                        int btcStatus = Country.SAME;
                        if (oldCountry.btc != -1) {
                            if (exchange.bitcoin > oldCountry.btc) {
                                btcStatus = Country.RISE;
                            } else if (exchange.bitcoin < oldCountry.btc) {
                                btcStatus = Country.DROP;
                            }
                        }

                        int ethStatus = Country.SAME;
                        if (oldCountry.eth != -1) {
                            if (exchange.bitcoin > oldCountry.btc) {
                                ethStatus = Country.RISE;
                            } else if (exchange.bitcoin < oldCountry.btc) {
                                ethStatus = Country.DROP;
                            }
                        }
                        oldCountry.btcStatus = btcStatus;
                        oldCountry.ethStatus = ethStatus;

                        oldCountry.eth = exchange.ethereum;
                        oldCountry.btc = exchange.bitcoin;
                        oldCountry.refreshedAt = System.currentTimeMillis();
                        return oldCountry;
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<History> loadDailyHistory(Country country, String to) {
        return getApi().getDayHistory(country.code, to)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<History> loadHourlyHistory(Country country, String to) {
        return getApi().getHourHistory(country.code, to)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
