package com.androidstudy.andelatrackchallenge.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by anonymous on 11/4/17.
 */

@Entity
public class DataDb {
    @Id
    long id;
    public long time;
    public float close;
    public float open;

    public ToOne<HistoryDb> historyData;

    public static DataDb of(Data data) {
        DataDb dataDb = new DataDb();
        dataDb.close = data.close;
        dataDb.time = data.time;
        dataDb.open = data.open;

        return dataDb;
    }
}

