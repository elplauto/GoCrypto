package fr.elplauto.gocrypto.ui.wallet;

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
import java.util.ArrayList;
import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.Wallet;

public class WalletFragment extends Fragment {

    private static final String TAG = "WalletFragment";
    private DBManager dbManager;
    LineChart chart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_wallet, container, false);

        chart = (LineChart) root.findViewById(R.id.chartWallet);

        dbManager = new DBManager(getContext());
        Wallet wallet = dbManager.getWallet();
        drawChart(wallet.getHistory());
        
        return root;
    }

    private void drawChart(List<History> historyList) {
        List <Entry> entries = new ArrayList<>();
        int index = 0;
        for (History history : historyList) {
            entries.add(new Entry(index, history.getValue().floatValue()));
            index++;
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
    }




}

class MyValueFormatter extends ValueFormatter {

    private List<Entry> entries;
    private Entry maxEntry;
    private Entry minEntry;

    public MyValueFormatter(List<Entry> entries) {
        this.entries = entries;
        this.maxEntry = getMaxEntry();
        this.minEntry = getMinEntry();
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
            return entry.getY()+"";
        } else {
            return "";
        }
    }
}