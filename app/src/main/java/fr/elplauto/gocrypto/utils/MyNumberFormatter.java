package fr.elplauto.gocrypto.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class MyNumberFormatter {

    public static String decimalPriceFormat(Double price) {
        int fractionDigits = 0;
        double inv = 1d / price;
        while (inv > 0.1) {
            inv = inv / 10d;
            fractionDigits = fractionDigits + 1;
        }
        fractionDigits += 2;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setCurrency(Currency.getInstance("USD"));
        format.setMinimumFractionDigits(fractionDigits);
        format.setMaximumFractionDigits(fractionDigits);
        return format.format(price);
    }

    public static String formatNumber(Double number) {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        return format.format(number);
    }
}
