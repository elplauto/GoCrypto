package fr.elplauto.gocrypto.ui.trends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.serviceCoinMarketCap.ServiceCoinMarketCap;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Crypto;

public class TrendsFragment extends Fragment implements CryptoAdapter.OnCryptoClickListener, ServiceCoinMarketCap.CMCCallbackListener {

    private static final String TAG = "TrendsFragment";
    private DBManager dbManager;
    private TrendsViewModel trendsViewModel;
    private RecyclerView recyclerView;
    final CryptoAdapter.OnCryptoClickListener onCryptoClickListener = this;
    private SwipeRefreshLayout swipeContainer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trendsViewModel = ViewModelProviders.of(this).get(TrendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trends, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_trends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        // recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        /*trendsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCryptoFromCMC();
            }
        });

        dbManager = new DBManager(getContext());
        loadCryptoFromDB();

        return root;
    }

    private void loadCryptoFromCMC() {
        ServiceCoinMarketCap.loadAllCrypto(getContext(), this);
    }

    private void loadCryptoFromDB() {
        List<Crypto> cryptoList = dbManager.getAllCrypto();
        CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCryptoClick(int position) {

    }


    @Override
    public void onCMCCallback(final List<Crypto> cryptoList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext());
                recyclerView.setAdapter(mAdapter);
                swipeContainer.setRefreshing(false);
                dbManager.replaceCryptoList(cryptoList);
            }
        });
    }
}