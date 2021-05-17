package fr.elplauto.gocrypto.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.LoginStatus;
import fr.elplauto.gocrypto.model.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletService {

    private static final String TAG = "WalletService";
    public static void getWallet(Context context, final WalletServiceCallbackListener walletServiceCallbackListener, String username) {

        OkHttpClient client = new OkHttpClient();
        String url = context.getResources().getString(R.string.server_base_url);
        url = url.concat("/wallet?username=" + username);
        Log.d(TAG, "wallet url : " + url);

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
                    Wallet wallet = gson.fromJson(json.toString(), Wallet.class);
                    walletServiceCallbackListener.onWalletServiceCallback(wallet);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface WalletServiceCallbackListener {
        void onWalletServiceCallback(Wallet wallet);
    }
}
