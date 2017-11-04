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
        countries.add(new Country("United States", "US Dollar", "USD"));
        countries.add(new Country("European Union", "Euro", "EUR"));
        countries.add(new Country("United Kingdom", "Sterling Pound", "GBP"));
        countries.add(new Country("India", "Rupees", "INR"));
        countries.add(new Country("Japan", "Yen", "JPY"));
        countries.add(new Country("Australia", "Australian Dollar", "AUD"));
        countries.add(new Country("Russia", "Russian Ruble", "RUB"));
        countries.add(new Country("Brazil", "Brazilian Real", "BRL"));
        countries.add(new Country("Mexico", "Mexican Peso", "MXN"));
        countries.add(new Country("Switzerland", "Swiss Franc", "CHF"));
        countries.add(new Country("China", "Yuan Renminbi", "CNY"));
        countries.add(new Country("Canada", "Canadian Dollar", "CAD"));
        countries.add(new Country("South Africa", "Rand", "ZAR"));
        countries.add(new Country("Turkey", "Turkish Lira", "TRY"));
        countries.add(new Country("Israel", "New Israeli Sheqel", "ILS"));
        countries.add(new Country("Taiwan", "New Taiwan Dollar", "TWD"));
        countries.add(new Country("New Zealand", "New Zealand Dollar", "NZD"));
        countries.add(new Country("Hong Kong", "Hong Kong Dollar", "HKD"));
        countries.add(new Country("Sweden", "Swedish Krona", "SEK"));
        countries.add(new Country("Poland", "Zloty", "PLN"));
        countries.add(new Country("Nigeria", "Naira", "NGN"));

        // Kenya added for Patriotic reasons. Mkenya daima!!!
        countries.add(new Country("Kenya", "Kenyan Shilling", "KES"));
    }
}
