package fr.elplauto.gocrypto.ui.trends;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
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
    private List<Crypto> cryptoList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trendsViewModel = ViewModelProviders.of(this).get(TrendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trends, container, false);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

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

        swipeContainer = root.findViewById(R.id.swipeContainer);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trends, menu);
        super.onCreateOptionsMenu(menu,inflater);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit:" + query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange:" + newText);
                List<Crypto> filteredCryptoList = filterCryptoList(newText);
                CryptoAdapter mAdapter = new CryptoAdapter(filteredCryptoList, onCryptoClickListener, getContext());
                recyclerView.setAdapter(mAdapter);
                return false;
            }
        });

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            }
        }

    }

    private void loadCryptoFromCMC() {
        ServiceCoinMarketCap.loadAllCrypto(getContext(), this);
    }

    private void loadCryptoFromDB() {
        cryptoList = dbManager.getAllCrypto();
        CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCryptoClick(int position) {

    }


    @Override
    public void onCMCCallback(final List<Crypto> cryptoList) {
        this.cryptoList = cryptoList;
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

    private List<Crypto> filterCryptoList(String filter) {
        List<Crypto> filteredList = new ArrayList<>();
        for (Crypto crypto : cryptoList) {
            if (crypto.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(crypto);
            }
        }
        return filteredList;
    }
}