package fr.elplauto.gocrypto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import fr.elplauto.gocrypto.api.CryptoDetailsService;
import fr.elplauto.gocrypto.api.CryptoDetailsService.CryptoDetailsServiceCallbackListener;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.Wallet;
import fr.elplauto.gocrypto.utils.PriceFormatter;

public class CryptoDetailsActivity extends AppCompatActivity implements CryptoDetailsServiceCallbackListener {

    private static final String TAG = "CryptoDetailsActivity";
    Toolbar toolbar;
    LineChart chart;
    TextView cryptoPrice;
    TextView btn_1h;
    TextView btn_7d;
    Wallet wallet;
    Crypto crypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_details);

        toolbar = findViewById(R.id.toolbar_crypto_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chart = findViewById(R.id.chartCrypto);
        cryptoPrice = findViewById(R.id.cryptoPrice);
        btn_1h = findViewById(R.id.btn_1h_crypto);
        btn_7d = findViewById(R.id.btn_7d_crypto);

        btn_1h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChart1h();
            }
        });

        btn_7d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChart7d();
            }
        });

        String chartDisplayPeriod = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("chartPeriod", "1h");
        if (chartDisplayPeriod.equals("1h")) {
            btn_1h.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else {
            btn_7d.setTextColor(ContextCompat.getColor(this, R.color.blue));
        }

        Bundle b = getIntent().getExtras();
        int cryptoId = b.getInt("crypto_id");

        CryptoDetailsService.getCryptoDetails(this, cryptoId);
    }

    @Override
    public void onCryptoDetailsServiceCallback(final Crypto crypto) {
        this.crypto = crypto;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(crypto.getName());
                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                format.setCurrency(Currency.getInstance("USD"));
                Double price = crypto.getHistory1h().get(0).getValue();
                String formattedPrice = formatPrice(price);
                cryptoPrice.setText(formattedPrice);
                drawChart(crypto);
            }
        });
    }

    private void displayChart1h() {
        btn_1h.setTextColor(ContextCompat.getColor(this, R.color.blue));
        btn_7d.setTextColor(ContextCompat.getColor(this, R.color.black));
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString("chartPeriod", "1h");
        editor.apply();
        if (this.crypto != null) {
            drawChart(crypto);
        }
    }

    private void displayChart7d() {
        btn_7d.setTextColor(ContextCompat.getColor(this, R.color.blue));
        btn_1h.setTextColor(ContextCompat.getColor(this, R.color.black));
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString("chartPeriod", "7d");
        editor.apply();
        if (this.crypto != null) {
            drawChart(crypto);
        }
    }

    private void drawChart(Crypto crypto) {

        Log.d(TAG, "drawChart");

        String chartDisplayPeriod = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("chartPeriod", "1h");
        List<History> historyList;
        if (chartDisplayPeriod.equals("1h")) {
            historyList = crypto.getHistory1h();
        } else {
            historyList = crypto.getHistory7d();
        }
        Log.d(TAG, "Nb points: " + historyList.size());

        List <Entry> entries = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            int reverseIndex = historyList.size() - 1 - i;
            float value = historyList.get(reverseIndex).getValue().floatValue();
            entries.add(new Entry(i, value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setValueFormatter(new PriceFormatter(entries));
        dataSet.setDrawCircles(false);
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(Color.BLACK);
        lineData.setValueTextSize(10f);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate(); // refresh
        chart.notifyDataSetChanged();
    }

    private String formatPrice(Double price) {
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
}