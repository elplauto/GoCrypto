package fr.elplauto.gocrypto.api;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.DataSearchAllCrypto;
import fr.elplauto.gocrypto.model.Leaderboard;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaderboardService {

    private static final String TAG = "LeaderboardService";
    public static void getLeaderboard(final LeaderboardServiceCallbackListener leaderboardServiceCallbackListener) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://go-crypto.herokuapp.com/leaderboard";
        Log.d(TAG, "leaderboard url : " + url);

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
                    Leaderboard leaderboard = gson.fromJson(json.toString(), Leaderboard.class);
                    leaderboardServiceCallbackListener.onLeaderboardServiceCallback(leaderboard);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface LeaderboardServiceCallbackListener {
        void onLeaderboardServiceCallback(Leaderboard leaderboard);
    }
}
