package fr.elplauto.gocrypto.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PriceFormatter extends ValueFormatter {

    private List<Entry> entries;
    private Entry maxEntry;
    private Entry minEntry;
    private NumberFormat format;

    public PriceFormatter(List<Entry> entries) {
        this.entries = entries;
        if (entries.size() > 0) {
            this.maxEntry = getMaxEntry();
            this.minEntry = getMinEntry();
        }
        this.format = NumberFormat.getCurrencyInstance(Locale.US);
        this.format.setCurrency(Currency.getInstance("USD"));
    }

    public Entry getMaxEntry() {
        Entry max = this.entries.get(0);
        for (Entry entry : this.entries) {
            if (entry.getY() > max.getY()) {
                max = entry;
            }
        }
        return max;
    }

    public Entry getMinEntry() {
        Entry min = this.entries.get(0);
        for (Entry entry : this.entries) {
            if (entry.getY() < min.getY()) {
                min = entry;
            }
        }
        return min;
    }

    @Override
    public String getPointLabel(Entry entry) {
        if (entry.equals(maxEntry) || entry.equals(minEntry)) {
            return formatPrice((double) entry.getY());
        } else {
            return "";
        }
    }

    private String formatPrice(Double price) {
        int fractionDigits = 0;
        double inv = 1d / price;
        while (inv > 0.1) {
            inv = inv / 10d;
            fractionDigits = fractionDigits + 1;
        }
        fractionDigits += 2;

        format.setMinimumFractionDigits(fractionDigits);
        format.setMaximumFractionDigits(fractionDigits);
        return format.format(price);
    }
}
