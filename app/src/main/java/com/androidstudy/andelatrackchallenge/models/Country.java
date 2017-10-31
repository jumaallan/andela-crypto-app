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
    @DrawableRes
    public int flagRes;
    public String name;
    public String currency;
    public String code;

    // set after exchange rate is got from crypto-compare api
    // -1 means not set
    public float btc = -1f;
    public float eth = -1f;
    public long refreshedAt = -1L;

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
        btc = in.readFloat();
        eth = in.readFloat();
        refreshedAt = in.readLong();
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
        parcel.writeFloat(btc);
        parcel.writeFloat(eth);
        parcel.writeLong(refreshedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (id != country.id) return false;
        if (flagRes != country.flagRes) return false;
        if (Float.compare(country.btc, btc) != 0) return false;
        if (Float.compare(country.eth, eth) != 0) return false;
        if (refreshedAt != country.refreshedAt) return false;
        if (!name.equals(country.name)) return false;
        if (!currency.equals(country.currency)) return false;
        return code.equals(country.code);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + flagRes;
        result = 31 * result + name.hashCode();
        result = 31 * result + currency.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + (btc != +0.0f ? Float.floatToIntBits(btc) : 0);
        result = 31 * result + (eth != +0.0f ? Float.floatToIntBits(eth) : 0);
        result = 31 * result + (int) (refreshedAt ^ (refreshedAt >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", flagRes=" + flagRes +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", btc=" + btc +
                ", eth=" + eth +
                ", refreshedAt=" + refreshedAt +
                '}';
    }
}
