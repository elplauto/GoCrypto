package fr.elplauto.gocrypto.ui.cryptoDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.CryptoDetailsService;
import fr.elplauto.gocrypto.api.CryptoDetailsService.CryptoDetailsServiceCallbackListener;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.Wallet;
import fr.elplauto.gocrypto.ui.trade.TradeActivity;
import fr.elplauto.gocrypto.utils.MyNumberFormatter;

public class CryptoDetailsActivity extends AppCompatActivity implements CryptoDetailsServiceCallbackListener {

    private static final String TAG = "CryptoDetailsActivity";
    Toolbar toolbar;
    LineChart chart;
    TextView cryptoPrice;
    TextView btn_1h;
    TextView btn_7d;
    TextView max_price;
    TextView min_price;
    Wallet wallet;
    Crypto crypto;
    ImageView arrowUpDown;
    TextView percentChangeTextView;
    ImageView cryptoIcon;
    Button tradeBtn;
    private SwipeRefreshLayout swipeContainer;
    CryptoDetailsServiceCallbackListener self = this;

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
        arrowUpDown = findViewById(R.id.arrowUpDownImg);
        percentChangeTextView = findViewById(R.id.percentChangeText);
        swipeContainer = findViewById(R.id.swipeCryptoDetails);
        cryptoIcon = findViewById(R.id.cryptoIcon);
        min_price = findViewById(R.id.minPrice);
        max_price = findViewById(R.id.maxPrice);
        tradeBtn = findViewById(R.id.tradeBtn);
        tradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTradeActivity();
            }
        });

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
        final int cryptoId = b.getInt("crypto_id");

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CryptoDetailsService.getCryptoDetails(getApplicationContext(), self, cryptoId);
            }
        });

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                CryptoDetailsService.getCryptoDetails(getApplicationContext(), self, cryptoId);
            }
        });

    }

    @Override
    public void onCryptoDetailsServiceCallback(final Crypto crypto) {
        this.crypto = crypto;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(crypto.getSymbol() + " - " + crypto.getName());
                String imgUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+ crypto.getId() +".png";
                Picasso.get().load(imgUrl).into(cryptoIcon);
                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                format.setCurrency(Currency.getInstance("USD"));
                Double price = crypto.getHistory1h().get(0).getValue();
                String formattedPrice = MyNumberFormatter.decimalPriceFormat(price);
                cryptoPrice.setText(formattedPrice);
                drawChart(crypto);
                swipeContainer.setRefreshing(false);
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

        if (historyList.size() > 0) {
            updatePercentChange(historyList);
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            int reverseIndex = historyList.size() - 1 - i;
            float value = historyList.get(reverseIndex).getValue().floatValue();
            entries.add(new Entry(i, value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setDrawValues(false);
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
        chart.getLegend().setEnabled(false);
        chart.setVisibleXRange(entries.size() - 1, entries.size() - 1);
        chart.invalidate();
        chart.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updatePercentChange(List<History> histories) {
        Double oldValue = histories.get(0).getValue();
        Double newValue = histories.get(histories.size() - 1).getValue();
        final Double percentChange = ((oldValue / newValue) - 1) * 100;
        final String progressionPercent = String.format("%.02f", Math.abs(percentChange)) + "%";
        Comparator<History> comparator = new Comparator<History>() {
            @Override
            public int compare(History o1, History o2) {
                return (o1.getValue().compareTo(o2.getValue()));
            }
        };

        final Double maxValue = Collections.max(histories, comparator).getValue();
        final Double minValue = Collections.min(histories, comparator).getValue();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentChangeTextView.setText(progressionPercent);
                if (percentChange >= 0) {
                    arrowUpDown.setImageResource(R.drawable.up_arrow);
                } else {
                    arrowUpDown.setImageResource(R.drawable.down_arrow);
                }

                min_price.setText("MIN " + MyNumberFormatter.decimalPriceFormat(minValue));
                max_price.setText("MAX " + MyNumberFormatter.decimalPriceFormat(maxValue));
            }
        });

    }

    void loadTradeActivity() {
        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
        Bundle b = new Bundle();
        b.putInt("crypto_id", crypto.getId());
        intent.putExtras(b);
        startActivity(intent);
    }
}