package com.androidstudy.andelatrackchallenge.models;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * Created by anonymous on 11/4/17.
 */

@Entity
public class HistoryDb {
    @Id
    long id;
    public String code;
    public String to;
    public long refreshedAt;
    public String response;
    public int type;
    public long timeTo;
    public long timeFrom;

    // Relation, not de/serialized
    @Backlink
    public ToMany<DataDb> data;

    public static HistoryDb of(History history, String code, String to) {
        HistoryDb historyDb = new HistoryDb();
        historyDb.code = code;
        historyDb.to = to;
        historyDb.refreshedAt = System.currentTimeMillis();
        historyDb.type = history.type;
        historyDb.response = history.response;
        historyDb.timeTo = history.timeTo;
        historyDb.timeFrom = history.timeFrom;

        for (Data datum : history.data) {
            historyDb.data.add(DataDb.of(datum));
        }

        return historyDb;
    }
}

