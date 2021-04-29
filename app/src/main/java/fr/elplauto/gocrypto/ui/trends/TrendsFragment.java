package fr.elplauto.gocrypto.ui.trends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.serviceCoinMarketCap.ServiceCoinMarketCap;
import fr.elplauto.gocrypto.database.DBHelper;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchCrypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchAllCrypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchUsd;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrendsFragment extends Fragment implements CryptoAdapter.OnCryptoClickListener {

    private static final String TAG = "TrendsFragment";
    private static final String DATABASE_NAME = "gocrypto.db";

    private DBHelper cryptoDB;

    private TrendsViewModel trendsViewModel;
    private RecyclerView recyclerView;
    private List<DataSearchCrypto> dataSearchCryptos;
    final CryptoAdapter.OnCryptoClickListener onCryptoClickListener = this;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trendsViewModel = ViewModelProviders.of(this).get(TrendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trends, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_trends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        /*trendsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        loadCryptoFromDB();

        return root;
    }

    private void reloadCryptoFromCMC() {
        ServiceCoinMarketCap.loadAllCrypto(getContext());
        loadCryptoFromDB();
    }

    private void loadCryptoFromDB() {
        String dbpath = getContext().getDatabasePath(DATABASE_NAME).getPath();
        Log.d(TAG, dbpath);
        cryptoDB = new DBHelper(getContext());
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbpath,  null, null);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Crypto> cryptoList = cryptoDB.getAllCrypto();
                        if (cryptoList.size() > 0) {
                            CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext());
                            recyclerView.setAdapter(mAdapter);
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onCryptoClick(int position) {

    }



}