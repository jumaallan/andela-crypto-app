package com.androidstudy.andelatrackchallenge.adapter;

import com.androidstudy.andelatrackchallenge.models.DataDb;
import com.robinhood.spark.SparkAdapter;

import java.util.List;

/**
 * Created by anonymous on 11/4/17.
 */

public class HistorySparkAdapter extends SparkAdapter {
    private List<DataDb> yData;

    public HistorySparkAdapter(List<DataDb> yData) {
        this.yData = yData;
    }

    @Override
    public int getCount() {
        return yData == null ? 0 : yData.size();
    }

    @Override
    public DataDb getItem(int index) {
        return yData.get(index);
    }

    @Override
    public float getY(int index) {
        return getItem(index).close;
    }
}

