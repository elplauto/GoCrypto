package fr.elplauto.gocrypto.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.Crypto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CryptoTradeService {

    private static final String TAG = "CryptoTradeService";
    public static void sellCrypto(Context context, final CryptoDetailsServiceCallbackListener cryptoDetailsServiceCallbackListener, String username, Integer cryptoId, Double amount) {

        OkHttpClient client = new OkHttpClient();
        String url = context.getResources().getString(R.string.server_base_url);
        url = url.concat("https://go-crypto.herokuapp.com/sell?username=" + username + "&cryptoId="+ cryptoId + "&amount=" + amount);
        Log.d(TAG, "sell url : " + url);

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
                    Crypto crypto = gson.fromJson(json.toString(), Crypto.class);
                    cryptoDetailsServiceCallbackListener.onCryptoDetailsServiceCallback((crypto));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void buyCrypto(Context context, final CryptoDetailsServiceCallbackListener cryptoDetailsServiceCallbackListener, String username, Integer cryptoId, Double amount) {

        OkHttpClient client = new OkHttpClient();
        String url = context.getResources().getString(R.string.server_base_url);
        url = url.concat("/buy?username=" + username + "&cryptoId="+ cryptoId + "&amount=" + amount);
        Log.d(TAG, "buy url : " + url);

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
                    Crypto crypto = gson.fromJson(json.toString(), Crypto.class);
                    cryptoDetailsServiceCallbackListener.onCryptoDetailsServiceCallback((crypto));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface CryptoDetailsServiceCallbackListener {
        void onCryptoDetailsServiceCallback(Crypto crypto);
    }
}
