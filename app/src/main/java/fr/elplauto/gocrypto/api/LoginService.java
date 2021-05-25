package fr.elplauto.gocrypto.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.DataSearchAllCrypto;
import fr.elplauto.gocrypto.model.LoginStatus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginService {

    private static final String TAG = "LoginService";
    public static void loginOrRegister(Context context, final LoginServiceCallbackListener loginServiceCallbackListener, String username, String password) {

        OkHttpClient client = new OkHttpClient();
        String url = context.getResources().getString(R.string.server_base_url);
        url = url.concat("/loginOrRegister?username=" + username + "&password=" + password);
        Log.d(TAG, "login url : " + url);

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
                    LoginStatus loginStatus = gson.fromJson(json.toString(), LoginStatus.class);
                    loginServiceCallbackListener.onLoginServiceCallback(loginStatus);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface LoginServiceCallbackListener {
        void onLoginServiceCallback(LoginStatus loginStatus);
    }
}
