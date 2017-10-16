package com.androidstudy.andelatrackchallenge.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by anonymous on 10/17/17.
 */

@Entity
public class Country implements Parcelable {
    @Id
    long id;
    public String name;
    public String currency;
    public String code;
    @DrawableRes
    public int flagRes;

    public Country() {
    }

    public Country(String name, String currency, String code, @DrawableRes int flagRes) {
        this.name = name;
        this.currency = currency;
        this.code = code;
        this.flagRes = flagRes;
    }

    protected Country(Parcel in) {
        name = in.readString();
        currency = in.readString();
        code = in.readString();
        flagRes = in.readInt();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(currency);
        parcel.writeString(code);
        parcel.writeInt(flagRes);
    }
}
