package fr.elplauto.gocrypto.ui.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.WalletService;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.CryptoMerge;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.ui.trends.CryptoAdapter;
import fr.elplauto.gocrypto.utils.SessionManager;
import fr.elplauto.gocrypto.model.Wallet;

public class WalletFragment extends Fragment implements WalletService.WalletServiceCallbackListener, CryptoWalletAdapter.OnCryptoWalletClickListener {

    private static final String TAG = "WalletFragment";
    private DBManager dbManager;
    LineChart chart;
    TextView wallet_total_amount;
    TextView btn_1h;
    TextView btn_7d;
    ImageView arrowUpDown;
    TextView percentChangeTextView;
    Wallet wallet;
    private SwipeRefreshLayout swipeContainer;
    WalletService.WalletServiceCallbackListener self = this;
    TextView maxAmount;
    TextView minAmount;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        chart = root.findViewById(R.id.chartWallet);
        wallet_total_amount = root.findViewById(R.id.wallet_total_amount);
        arrowUpDown = root.findViewById(R.id.arrowUpDown);
        percentChangeTextView = root.findViewById(R.id.percentChangeTextView);
        swipeContainer = root.findViewById(R.id.swipeWallet);
        btn_1h = root.findViewById(R.id.btn_1h_wallet);
        btn_7d = root.findViewById(R.id.btn_7d_wallet);
        maxAmount = root.findViewById(R.id.maxAmount);
        minAmount = root.findViewById(R.id.minAmount);

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

        recyclerView = root.findViewById(R.id.recycler_view_wallet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        String chartDisplayPeriod = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("chartPeriod", "1h");
        if (chartDisplayPeriod.equals("1h")) {
            btn_1h.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else {
            btn_7d.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        }

        SessionManager sessionManager = SessionManager.getInstance(getContext());
        final String username = sessionManager.getUsername();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WalletService.getWallet(getContext(), self, username);
            }
        });

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                WalletService.getWallet(getContext(), self, username);
            }
        });

        dbManager = new DBManager(getContext());


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

        updatePercentChange(historyList);

        List <Entry> entries = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            int reverseIndex = historyList.size() - 1 - i;
            float value = historyList.get(reverseIndex).getValue().floatValue();
            entries.add(new Entry(i, value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
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
    public void onWalletServiceCallback(final Wallet wallet) {
        this.wallet = wallet;
        Double price = wallet.getHistory1h().get(0).getValue();
        final String formattedPrice = formatPrice(price);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wallet_total_amount.setText(formattedPrice);
                drawChart(wallet);
                displayCrypto(wallet.getCrypto());
                swipeContainer.setRefreshing(false);
            }
        });
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

    private void updatePercentChange(List<History> histories) {
        Double oldValue = histories.get(0).getValue();
        Double newValue = histories.get(histories.size() - 1).getValue();
        final double percentChange = ((oldValue / newValue) - 1) * 100;
        final String progressionPercent = String.format("%.02f", Math.abs(percentChange)) + "%";
        Comparator<History> comparator = new Comparator<History>() {
            @Override
            public int compare(History o1, History o2) {
                return (o1.getValue().compareTo(o2.getValue()));
            }
        };

        final Double maxValue = Collections.max(histories, comparator).getValue();
        final Double minValue = Collections.min(histories, comparator).getValue();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentChangeTextView.setText(progressionPercent);
                if (percentChange >= 0) {
                    arrowUpDown.setImageResource(R.drawable.up_arrow);
                } else {
                    arrowUpDown.setImageResource(R.drawable.down_arrow);
                }
                minAmount.setText("MIN " + formatPrice(minValue));
                maxAmount.setText("MAX " + formatPrice(maxValue));
            }
        });

    }

    private void displayCrypto(List<CryptoInWallet> cryptoList) {
        List<CryptoMerge> cryptoMergeList = new ArrayList<>();
        Map<Integer, Crypto> map = dbManager.getCryptoMap();
        for (int i = 0; i < 20; i++) {
            for (CryptoInWallet cryptoInWallet : cryptoList) {
                CryptoMerge cryptoMerge = new CryptoMerge(map.get(cryptoInWallet.getId()), cryptoInWallet);
                cryptoMergeList.add(cryptoMerge);
            }
        }


        CryptoWalletAdapter mAdapter = new CryptoWalletAdapter(cryptoMergeList, this, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCryptoClick(int position) {

    }
}

