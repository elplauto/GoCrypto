package fr.elplauto.gocrypto.ui.trends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.elplauto.gocrypto.BuildConfig;
import fr.elplauto.gocrypto.MainActivity;
import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.searchAllCrypto.Crypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchAllCrypto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrendsFragment extends Fragment implements CryptoAdapter.OnCryptoClickListener {

    private static final String TAG = "TrendsFragment";
    private static String apiKey = "8005261d-3361-4dd5-8b6b-4ff540e046d9";

    private TrendsViewModel trendsViewModel;

    private RecyclerView recyclerView;
    private List<Crypto> cryptos;
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

        loadAllCrypto();

        return root;
    }

    private void loadAllCrypto() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        Log.d(TAG, "loadAllCrypto url : " + url);

        Request request = new Request.Builder().url(url).addHeader("X-CMC_PRO_API_KEY", apiKey).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            Log.d(TAG, "JSON : " + json.toString());

                            Gson gson = new Gson();
                            DataSearchAllCrypto dataSearchAllCrypto = gson.fromJson(json.toString(),
                                    DataSearchAllCrypto.class);

                            cryptos = dataSearchAllCrypto.getCryptos();
                            if (cryptos != null && cryptos.size() > 0) {
                                CryptoAdapter mAdapter = new CryptoAdapter(cryptos, onCryptoClickListener, getContext());
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                recyclerView.setAdapter(null);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onCryptoClick(int position) {

    }
}