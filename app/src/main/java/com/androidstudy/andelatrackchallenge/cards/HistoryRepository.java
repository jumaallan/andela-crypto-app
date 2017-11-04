package com.androidstudy.andelatrackchallenge.cards;

import android.content.Context;

import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.HistoryDb;
import com.androidstudy.andelatrackchallenge.models.HistoryDb_;
import com.androidstudy.andelatrackchallenge.network.ApiClient;

import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by anonymous on 11/4/17.
 */

public class HistoryRepository {
    private BoxStore boxStore;

    private Box<HistoryDb> historyBox;

    public HistoryRepository(Context context, BoxStore boxStore) {
        this.boxStore = boxStore;

        historyBox = this.boxStore.boxFor(HistoryDb.class);
    }

    public Single<HistoryDb> getDailyHistoryFor(Country country, String to) {
        Single<HistoryDb> single;

        List<HistoryDb> historyDbList = historyBox.query()
                .equal(HistoryDb_.code, country.code)
                .and().equal(HistoryDb_.to, to)
                .filter(entity -> {
                    //todo change this asap. THis is only for testing!
                    long halfDay = 12 * 60 * 60 * 1000;
                    return new Date(entity.refreshedAt + halfDay)
                            .after(new Date(System.currentTimeMillis()));
                })
                .build()
                .find();

        // Allow stale history to pass though, edit and save back to DB. Ensures no repeated
        // country history are stored, saving space!
        HistoryDb historyDb;
        if (historyDbList.size() > 0
                && (historyDb = historyDbList.get(0)) != null) {
            single = Single.just(historyDb)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            single = ApiClient.loadDailyHistory(country, to)
                    .map(history -> HistoryDb.of(history, country.code, to))
                    .doOnSuccess(hD -> historyBox.put(hD));
        }

        return single;
    }
}
