package com.androidstudy.andelatrackchallenge.models;

/**
 * Created by anonymous on 11/1/17.
 */

import com.squareup.moshi.Json;

/**
 * Model class for json data format from https://cryptocompare.com/api/data/price
 */

public class Exchange {
    // Note: request format for this model class is ../data/price/fsym=USD&tsyms=ETH,BTC.
    // In case this format is not followed, an error is thrown by moshi.
    @Json(name = "ETH")
    public float ethereum;
    @Json(name = "BTC")
    public float bitcoin;

    public Exchange(float ethereum, float bitcoin) {
        this.ethereum = ethereum;
        this.bitcoin = bitcoin;
    }

    public Exchange() {
    }
}

