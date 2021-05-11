package fr.elplauto.gocrypto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.Wallet;

public class DBManager {

    private static final String TAG = "DBManager";
    private static final String DATABASE_NAME = "gocrypto.db";
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        this.context = context;
        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        Log.d(TAG, dbPath);
        this.dbHelper = new DBHelper(context);
        this.db = SQLiteDatabase.openOrCreateDatabase(dbPath,  null, null);
    }

    public void replaceCryptoList(List<Crypto> cryptoList) {
        dbHelper.onUpgrade(db,0,0);
        for (Crypto crypto : cryptoList) {
            dbHelper.insertCrypto(crypto);
        }
    }

    public List<Crypto> getAllCrypto() {
        return dbHelper.getAllCrypto();
    }

    public void saveWallet(Wallet wallet) {
        dbHelper.insertWallet(wallet);
    }

    public Wallet getWallet() {
        return dbHelper.getWallet();
    }





}
