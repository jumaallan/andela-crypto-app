package com.androidstudy.andelatrackchallenge.picker.currency;

import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;

import java.util.ArrayList;

/**
 * Created by anonymous on 10/17/17.
 */

public class Countries {
    public static final ArrayList<Country> countries = new ArrayList<>();

    static {
        countries.add(new Country("United States", "US Dollar", "USD", R.drawable.flag_us));
        countries.add(new Country("European Union", "Euro", "EUR", R.drawable.flag_eu));
        countries.add(new Country("United Kingdom", "Sterling Pound", "GBP", R.drawable.flag_gb));
        countries.add(new Country("India", "Rupees", "INR", R.drawable.flag_in));
        countries.add(new Country("Japan", "Yen", "JPY", R.drawable.flag_jp));
        countries.add(new Country("Australia", "Australian Dollar", "AUD", R.drawable.flag_au));
        countries.add(new Country("Russia", "Russian Ruble", "RUB", R.drawable.flag_ru));
        countries.add(new Country("Brazil", "Brazilian Real", "BRL", R.drawable.flag_br));
        countries.add(new Country("Mexico", "Mexican Peso", "MXN", R.drawable.flag_mx));
        countries.add(new Country("Switzerland", "Swiss Franc", "CHF", R.drawable.flag_ch));
        countries.add(new Country("China", "Yuan Renminbi", "CNY", R.drawable.flag_cn));
        countries.add(new Country("Canada", "Canadian Dollar", "CAD", R.drawable.flag_ca));
        countries.add(new Country("South Africa", "Rand", "ZAR", R.drawable.flag_za));
        countries.add(new Country("Turkey", "Turkish Lira", "TRY", R.drawable.flag_tr));
        countries.add(new Country("Israel", "New Israeli Sheqel", "ILS", R.drawable.flag_il));
        countries.add(new Country("Taiwan", "New Taiwan Dollar", "TWD", R.drawable.flag_tw));
        countries.add(new Country("New Zealand", "New Zealand Dollar", "NZD", R.drawable.flag_nz));
        countries.add(new Country("Hong Kong", "Hong Kong Dollar", "HKD", R.drawable.flag_hk));
        countries.add(new Country("Sweden", "Swedish Krona", "SEK", R.drawable.flag_se));
        countries.add(new Country("Poland", "Zloty", "PLN", R.drawable.flag_pl));
        countries.add(new Country("Nigeria", "Naira", "NGN", R.drawable.flag_ng));
    }
}
