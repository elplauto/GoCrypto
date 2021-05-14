package fr.elplauto.gocrypto.ui.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.WalletService;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.SessionManager;
import fr.elplauto.gocrypto.model.Wallet;

public class WalletFragment extends Fragment implements WalletService.WalletServiceCallbackListener {

    private static final String TAG = "WalletFragment";
    private DBManager dbManager;
    LineChart chart;
    TextView wallet_total_amount;
    TextView btn_1h;
    TextView btn_7d;
    Wallet wallet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        chart = root.findViewById(R.id.chartWallet);
        wallet_total_amount = root.findViewById(R.id.wallet_total_amount);
        btn_1h = root.findViewById(R.id.btn_1h_wallet);
        btn_7d = root.findViewById(R.id.btn_7d_wallet);

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

        String chartDisplayPeriod = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("chartPeriod", "1h");
        if (chartDisplayPeriod.equals("1h")) {
            btn_1h.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else {
            btn_7d.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        }

        dbManager = new DBManager(getContext());

        SessionManager sessionManager = SessionManager.getInstance(getContext());
        String username = sessionManager.getUsername();
        WalletService.getWallet(this, username);

        return root;
    }

    private void drawChart(Wallet wallet) {

        String chartDisplayPeriod = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("chartPeriod", "1h");
        List<History> historyList;
        if (chartDisplayPeriod.equals("1h")) {
            historyList = wallet.getHistory1h();
        } else {
            historyList = wallet.getHistory7d();
        }

        List <Entry> entries = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            int reverseIndex = historyList.size() - 1 - i;
            float value = historyList.get(reverseIndex).getValue().floatValue();
            entries.add(new Entry(i, value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setValueFormatter(new MyValueFormatter(entries));
        dataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
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

    @Override
    public void onWalletServiceCallback(Wallet wallet) {
        this.wallet = wallet;
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setCurrency(Currency.getInstance("USD"));
        Double price = wallet.getHistory1h().get(0).getValue();
        String formattedPrice = format.format(price);
        wallet_total_amount.setText(formattedPrice);
        drawChart(wallet);
    }

    private void displayChart1h() {
        btn_1h.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        btn_7d.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        SharedPreferences.Editor editor = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString("chartPeriod", "1h");
        editor.apply();
        if (this.wallet != null) {
            drawChart(wallet);
        }
    }

    private void displayChart7d() {
        btn_7d.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        btn_1h.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        SharedPreferences.Editor editor = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString("chartPeriod", "7d");
        editor.apply();
        if (this.wallet != null) {
            drawChart(wallet);
        }
    }

}

class MyValueFormatter extends ValueFormatter {

    private List<Entry> entries;
    private Entry maxEntry;
    private Entry minEntry;
    private NumberFormat format;

    public MyValueFormatter(List<Entry> entries) {
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
            return format.format(entry.getY());
        } else {
            return "";
        }
    }

}