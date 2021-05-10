package fr.elplauto.gocrypto.api.cryptoService;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.DataSearchAllCrypto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CryptoService {

    private static final String TAG = "CryptoService";
    public static void loadAllCrypto(Context context, final CryptoServiceCallbackListener cryptoServiceCallbackListener) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://go-crypto.herokuapp.com/crypto";
        Log.d(TAG, "crypto url : " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    Log.d(TAG, "JSON : " + json.toString());
                    Gson gson = new Gson();
                    DataSearchAllCrypto dataSearchAllCrypto = gson.fromJson(json.toString(), DataSearchAllCrypto.class);
                    List<Crypto> cryptoList = dataSearchAllCrypto.getData();
                    cryptoServiceCallbackListener.onCryptoServiceCallback(cryptoList);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface CryptoServiceCallbackListener {
        void onCryptoServiceCallback(List<Crypto> cryptoList);
    }
}
