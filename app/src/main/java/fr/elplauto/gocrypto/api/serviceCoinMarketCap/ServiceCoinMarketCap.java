package fr.elplauto.gocrypto.api.serviceCoinMarketCap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.elplauto.gocrypto.database.DBHelper;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchAllCrypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchCrypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchUsd;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceCoinMarketCap {

    private static final String TAG = "ServiceCoinMarketCap";
    private static String apiKey = "8005261d-3361-4dd5-8b6b-4ff540e046d9";
    private static final String DATABASE_NAME = "gocrypto.db";
    private static DBHelper cryptoDB;
    public static void loadAllCrypto(Context context) {

        String dbpath = context.getDatabasePath(DATABASE_NAME).getPath();
        Log.d(TAG, dbpath);
        cryptoDB = new DBHelper(context);
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbpath,  null, null);
        cryptoDB.onUpgrade(db,0,0);

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
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    Log.d(TAG, "JSON : " + json.toString());
                    Gson gson = new Gson();
                    DataSearchAllCrypto dataSearchAllCrypto = gson.fromJson(json.toString(), DataSearchAllCrypto.class);
                    addCryptoToDB(dataSearchAllCrypto);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void addCryptoToDB(DataSearchAllCrypto dataSearchAllCrypto) {
        for (DataSearchCrypto dataSearchCrypto : dataSearchAllCrypto.getDataSearchCryptos()) {
            Crypto crypto = dataSearchCryptoToCrypto(dataSearchCrypto);
            cryptoDB.insertCrypto(crypto);
        }
    }

    private static Crypto dataSearchCryptoToCrypto (DataSearchCrypto dataSearchCrypto) {
        Crypto crypto = new Crypto();
        crypto.setId(dataSearchCrypto.getId().toString());
        crypto.setName(dataSearchCrypto.getName());
        crypto.setSymbol(dataSearchCrypto.getSymbol());
        DataSearchUsd usd = dataSearchCrypto.getDataSearchQuote().getDataSearchUsd();
        crypto.setPrice(usd.getPrice());
        crypto.setPercentChange1h(usd.getPercentChange1h());
        crypto.setPercentChange24h(usd.getPercentChange24h());
        crypto.setPercentChange7d(usd.getPercentChange7d());
        crypto.setPercentChange30d(usd.getPercentChange30d());
        crypto.setPercentChange60d(usd.getPercentChange60d());
        crypto.setPercentChange90d(usd.getPercentChange90d());
        return crypto;
    }
}
