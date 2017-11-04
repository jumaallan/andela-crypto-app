package com.androidstudy.andelatrackchallenge.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.androidstudy.andelatrackchallenge.R;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by anonymous on 10/17/17.
 */

@Entity
public class Country implements Parcelable {
    public static final int RISE = 1;
    public static final int SAME = 0;
    public static final int DROP = -1;

    @Id
    public long id;
    public String name;
    public String currency;
    public String code;
    public int ethStatus = SAME;
    public int btcStatus = SAME;

    // set after exchange rate is got from crypto-compare api
    // -1 means not set
    public float btc = -1f;
    public float eth = -1f;
    public long refreshedAt = -1L;
    public boolean isFavorite = false;

    public Country() {
    }

    public Country(String name, String currency, String code) {
        this.name = name;
        this.currency = currency;
        this.code = code;
    }

    @DrawableRes
    public int getFlagRes() {
        switch (code) {
            case "USD":
                return R.drawable.flag_us;
            case "EUR":
                return R.drawable.flag_eu;
            case "GBP":
                return R.drawable.flag_gb;
            case "INR":
                return R.drawable.flag_in;
            case "JPY":
                return R.drawable.flag_jp;
            case "AUD":
                return R.drawable.flag_au;
            case "RUB":
                return R.drawable.flag_ru;
            case "BRL":
                return R.drawable.flag_br;
            case "MXN":
                return R.drawable.flag_mx;
            case "CHF":
                return R.drawable.flag_ch;
            case "CNY":
                return R.drawable.flag_cn;
            case "CAD":
                return R.drawable.flag_ca;
            case "ZAR":
                return R.drawable.flag_za;
            case "TRY":
                return R.drawable.flag_tr;
            case "ILS":
                return R.drawable.flag_il;
            case "TWD":
                return R.drawable.flag_tw;
            case "NZD":
                return R.drawable.flag_nz;
            case "HKD":
                return R.drawable.flag_hk;
            case "SEK":
                return R.drawable.flag_se;
            case "PLN":
                return R.drawable.flag_pl;
            case "NGN":
                return R.drawable.flag_ng;
            case "KES":
                return R.drawable.flag_ke;
            default:
                return R.drawable.ic_action_add_card;
        }
    }

    protected Country(Parcel in) {
        name = in.readString();
        currency = in.readString();
        code = in.readString();
        btcStatus = in.readInt();
        ethStatus = in.readInt();
        btc = in.readFloat();
        eth = in.readFloat();
        refreshedAt = in.readLong();
        isFavorite = in.readInt() != 0;
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
        parcel.writeInt(btcStatus);
        parcel.writeInt(ethStatus);
        parcel.writeFloat(btc);
        parcel.writeFloat(eth);
        parcel.writeLong(refreshedAt);
        parcel.writeInt(isFavorite ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (id != country.id) return false;
        if (Float.compare(country.btc, btc) != 0) return false;
        if (Float.compare(country.eth, eth) != 0) return false;
        if (country.ethStatus != ethStatus) return false;
        if (country.btcStatus != btcStatus) return false;
        if (refreshedAt != country.refreshedAt) return false;
        if (!name.equals(country.name)) return false;
        if (!currency.equals(country.currency)) return false;
        if (!country.isFavorite == isFavorite) return false;
        return code.equals(country.code);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + currency.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + btcStatus;
        result = 31 * result + ethStatus;
        result = 31 * result + (btc != +0.0f ? Float.floatToIntBits(btc) : 0);
        result = 31 * result + (eth != +0.0f ? Float.floatToIntBits(eth) : 0);
        result = 31 * result + (int) (refreshedAt ^ (refreshedAt >>> 32));
        result = 31 * result + (isFavorite ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", ethStatus=" + ethStatus +
                ", btcStatus=" + btcStatus +
                ", btc=" + btc +
                ", eth=" + eth +
                ", refreshedAt=" + refreshedAt +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
