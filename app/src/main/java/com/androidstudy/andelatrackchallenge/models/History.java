package com.androidstudy.andelatrackchallenge.models;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by anonymous on 11/4/17.
 */

public class History {
    // Constants
    public static final String SUCCESS = "Success";
    public static final int TYPE_SUCCESS = 100;

    @Json(name = "Response")
    public String response;
    @Json(name = "Type")
    public int type;
    @Json(name = "Data")
    public List<Data> data;
    @Json(name = "TimeTo")
    public long timeTo;
    @Json(name = "TimeFrom")
    public long timeFrom;
}

