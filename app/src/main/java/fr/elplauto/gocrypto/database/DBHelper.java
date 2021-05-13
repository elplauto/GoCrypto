package fr.elplauto.gocrypto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.Wallet;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final String DATABASE_NAME = "gocrypto.db";
    public static final String CRYPTO_TABLE_NAME = "crypto";
    public static final String CRYPTO_COLUMN_ID = "id";
    public static final String CRYPTO_COLUMN_ID_CRYPTO = "id_crypto";
    public static final String CRYPTO_COLUMN_NAME = "name";
    public static final String CRYPTO_COLUMN_SYMBOL = "symbol";
    public static final String CRYPTO_COLUMN_PRICE = "price";
    public static final String CRYPTO_COLUMN_CHANGE_1H = "percentChange1h";
    public static final String CRYPTO_COLUMN_CHANGE_24H = "percentChange24h";
    public static final String CRYPTO_COLUMN_CHANGE_7D = "percentChange7d";
    public static final String CRYPTO_COLUMN_CHANGE_30D = "percentChange30d";
    public static final String CRYPTO_COLUMN_CHANGE_60D = "percentChange60d";
    public static final String CRYPTO_COLUMN_CHANGE_90D = "percentChange90d";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS history_1h_crypto");
        db.execSQL("DROP TABLE IF EXISTS history_7d_crypto");
        db.execSQL("DROP TABLE IF EXISTS " + CRYPTO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS history_1h_wallet");
        db.execSQL("DROP TABLE IF EXISTS history_7d_wallet");
        db.execSQL("DROP TABLE IF EXISTS cryptoWallet");
        db.execSQL("DROP TABLE IF EXISTS wallet");

        db.execSQL("CREATE TABLE " + CRYPTO_TABLE_NAME +
                "(" + CRYPTO_COLUMN_ID + " integer primary key," +
                CRYPTO_COLUMN_ID_CRYPTO + " text," +
                CRYPTO_COLUMN_NAME + " text," +
                CRYPTO_COLUMN_SYMBOL + " text," +
                CRYPTO_COLUMN_PRICE + " float," +
                CRYPTO_COLUMN_CHANGE_1H + " float," +
                CRYPTO_COLUMN_CHANGE_24H + " float," +
                CRYPTO_COLUMN_CHANGE_7D + " float," +
                CRYPTO_COLUMN_CHANGE_30D + " float," +
                CRYPTO_COLUMN_CHANGE_60D + " float," +
                CRYPTO_COLUMN_CHANGE_90D + " float)");

        db.execSQL("CREATE TABLE history_1h_crypto "+
                "(id integer primary key," +
                "timestamp string," +
                "value float," +
                "id_crypto," +
                "FOREIGN KEY(id_crypto) REFERENCES crypto(id))");

        db.execSQL("CREATE TABLE history_7d_crypto "+
                "(id integer primary key," +
                "timestamp string," +
                "value float," +
                "id_crypto," +
                "FOREIGN KEY(id_crypto) REFERENCES crypto(id))");

        db.execSQL("CREATE TABLE wallet "+
                        "(id integer primary key," +
                        "usd float)");

        db.execSQL("CREATE TABLE history_1h_wallet "+
                "(id integer primary key," +
                "timestamp string," +
                "value float)");

        db.execSQL("CREATE TABLE history_7d_wallet "+
                "(id integer primary key," +
                "timestamp string," +
                "value float)");

        db.execSQL("CREATE TABLE cryptoWallet "+
                "(id integer primary key," +
                "id_crypto integer," +
                "amount float," +
                "purchasing_price float," +
                "id_wallet integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public boolean insertCrypto (Crypto crypto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CRYPTO_COLUMN_ID_CRYPTO, crypto.getId());
        contentValues.put(CRYPTO_COLUMN_NAME, crypto.getName());
        contentValues.put(CRYPTO_COLUMN_SYMBOL, crypto.getSymbol());
        contentValues.put(CRYPTO_COLUMN_PRICE, crypto.getPrice());
        contentValues.put(CRYPTO_COLUMN_CHANGE_1H, crypto.getPercentChange1h());
        contentValues.put(CRYPTO_COLUMN_CHANGE_24H, crypto.getPercentChange24h());
        contentValues.put(CRYPTO_COLUMN_CHANGE_7D, crypto.getPercentChange7d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_30D, crypto.getPercentChange30d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_60D, crypto.getPercentChange60d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_90D, crypto.getPercentChange90d());
        long id = db.insert(CRYPTO_TABLE_NAME, null, contentValues);

        /**for (History history : crypto.getHistory1h()) {
            ContentValues contentValuesHistory1h = new ContentValues();
            contentValuesHistory1h.put("timestamp", history.getTimestamp());
            contentValuesHistory1h.put("value", history.getValue());
            contentValuesHistory1h.put("id_crypto", id);
            db.insert("history_1h_crypto", null, contentValuesHistory1h);
        }

        for (History history : crypto.getHistory7d()) {
            ContentValues contentValuesHistory7d = new ContentValues();
            contentValuesHistory7d.put("timestamp", history.getTimestamp());
            contentValuesHistory7d.put("value", history.getValue());
            contentValuesHistory7d.put("id_crypto", id);
            db.insert("history_7d_crypto", null, contentValuesHistory7d);
        }**/

        return true;
    }

    public Crypto getCrypto(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from crypto where id_crypto="+id+"", null );

        Crypto crypto = new Crypto();
        int cryptoId = res.getInt(res.getColumnIndex(CRYPTO_COLUMN_ID_CRYPTO));
        crypto.setId(res.getInt(res.getColumnIndex(CRYPTO_COLUMN_ID_CRYPTO)));
        crypto.setName(res.getString(res.getColumnIndex(CRYPTO_COLUMN_NAME)));
        crypto.setSymbol(res.getString(res.getColumnIndex(CRYPTO_COLUMN_SYMBOL)));
        crypto.setPrice(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_PRICE)));
        crypto.setPercentChange1h(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_1H)));
        crypto.setPercentChange24h(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_24H)));
        crypto.setPercentChange7d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_7D)));
        crypto.setPercentChange30d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_30D)));
        crypto.setPercentChange60d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_60D)));
        crypto.setPercentChange90d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_90D)));

        List<History> history1h = new ArrayList<>();
        Cursor resHistory =  db.rawQuery( "select * from history_1h_crypto where id=" + cryptoId , null );
        resHistory.moveToFirst();
        while(!resHistory.isAfterLast()) {
            History history = new History();
            history.setTimestamp(resHistory.getString(resHistory.getColumnIndex("timestamp")));
            history.setValue(resHistory.getDouble(resHistory.getColumnIndex("value")));
            history1h.add(history);
            resHistory.moveToNext();
        }
        crypto.setHistory1h(history1h);

        List<History> history7d = new ArrayList<>();
        resHistory =  db.rawQuery( "select * from history_7d_crypto where id=" + cryptoId , null );
        resHistory.moveToFirst();
        while(!resHistory.isAfterLast()) {
            History history = new History();
            history.setTimestamp(resHistory.getString(resHistory.getColumnIndex("timestamp")));
            history.setValue(resHistory.getDouble(resHistory.getColumnIndex("value")));
            history7d.add(history);
            resHistory.moveToNext();
        }
        crypto.setHistory7d(history7d);
        return crypto;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CRYPTO_TABLE_NAME);
        return numRows;
    }

    public boolean updateCrypto(Crypto crypto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CRYPTO_COLUMN_ID_CRYPTO, crypto.getId());
        contentValues.put(CRYPTO_COLUMN_NAME, crypto.getName());
        contentValues.put(CRYPTO_COLUMN_SYMBOL, crypto.getSymbol());
        contentValues.put(CRYPTO_COLUMN_PRICE, crypto.getPrice());
        contentValues.put(CRYPTO_COLUMN_CHANGE_1H, crypto.getPercentChange1h());
        contentValues.put(CRYPTO_COLUMN_CHANGE_24H, crypto.getPercentChange24h());
        contentValues.put(CRYPTO_COLUMN_CHANGE_7D, crypto.getPercentChange7d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_30D, crypto.getPercentChange30d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_60D, crypto.getPercentChange60d());
        contentValues.put(CRYPTO_COLUMN_CHANGE_90D, crypto.getPercentChange90d());
        db.update(CRYPTO_TABLE_NAME, contentValues, "id = ? ", new String[] { crypto.getId()+"" } );
        return true;
    }

    public Integer deleteCrypto (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CRYPTO_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Crypto> getAllCrypto() {
        ArrayList<Crypto> cryptoList = new ArrayList<Crypto>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from crypto", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            Crypto crypto = new Crypto();
            crypto.setId(res.getInt(res.getColumnIndex(CRYPTO_COLUMN_ID_CRYPTO)));
            crypto.setName(res.getString(res.getColumnIndex(CRYPTO_COLUMN_NAME)));
            crypto.setSymbol(res.getString(res.getColumnIndex(CRYPTO_COLUMN_SYMBOL)));
            crypto.setPrice(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_PRICE)));
            crypto.setPercentChange1h(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_1H)));
            crypto.setPercentChange24h(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_24H)));
            crypto.setPercentChange7d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_7D)));
            crypto.setPercentChange30d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_30D)));
            crypto.setPercentChange60d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_60D)));
            crypto.setPercentChange90d(res.getDouble(res.getColumnIndex(CRYPTO_COLUMN_CHANGE_90D)));
            crypto.setHistory1h(new ArrayList<History>());
            crypto.setHistory7d(new ArrayList<History>());
            cryptoList.add(crypto);
            res.moveToNext();
        }
        return cryptoList;
    }

    public void deleteAllCrypto() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from history_1h_crypto");
        db.execSQL("delete from history_7d_crypto");
        db.execSQL("delete from crypto");
    }

    public boolean insertWallet (Wallet wallet) {
        SQLiteDatabase db = this.getWritableDatabase();

        //clear existing wallet
        db.execSQL("delete from history");
        db.execSQL("delete from cryptoWallet");
        db.execSQL("delete from wallet");

        //add wallet
        ContentValues contentValuesWallet = new ContentValues();
        contentValuesWallet.put("usd", wallet.getUsd());
        db.insert("wallet", null, contentValuesWallet);

        for (CryptoInWallet crypto : wallet.getCrypto()) {
            ContentValues contentValuesCrypto = new ContentValues();
            contentValuesCrypto.put("id_crypto", crypto.getId());
            contentValuesCrypto.put("amount", crypto.getAmount());
            contentValuesCrypto.put("purchasing_price", crypto.getPurchasingPrice());
            db.insert("cryptoWallet", null, contentValuesCrypto);
        }

        for (History history : wallet.getHistory1h()) {
            ContentValues contentValuesHistory = new ContentValues();
            contentValuesHistory.put("timestamp", history.getTimestamp());
            contentValuesHistory.put("value", history.getValue());
            db.insert("history_1h_wallet", null, contentValuesHistory);
        }

        for (History history : wallet.getHistory7d()) {
            ContentValues contentValuesHistory = new ContentValues();
            contentValuesHistory.put("timestamp", history.getTimestamp());
            contentValuesHistory.put("value", history.getValue());
            db.insert("history_7d_wallet", null, contentValuesHistory);
        }

        return true;
    }

    public Wallet getWallet() {
        SQLiteDatabase db = this.getReadableDatabase();
        Wallet wallet = new Wallet();

        //get usd
        Cursor res =  db.rawQuery( "select * from wallet", null );
        res.moveToFirst();
        wallet.setUsd(res.getDouble(res.getColumnIndex("usd")));

        //get crypto
        res =  db.rawQuery( "select * from cryptoWallet", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            CryptoInWallet crypto = new CryptoInWallet();
            crypto.setId(res.getInt(res.getColumnIndex("id_crypto")));
            crypto.setAmount(res.getDouble(res.getColumnIndex("amount")));
            crypto.setPurchasingPrice(res.getDouble(res.getColumnIndex("purchasing_price")));
            wallet.getCrypto().add(crypto);
            res.moveToNext();
        }

        //get history 1h
        res =  db.rawQuery( "select * from history_1h_wallet", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            History history = new History();
            history.setTimestamp(res.getString(res.getColumnIndex("timestamp")));
            history.setValue(res.getDouble(res.getColumnIndex("value")));
            wallet.getHistory1h().add(history);
            res.moveToNext();
        }

        //get history 7d
        res =  db.rawQuery( "select * from history_7d_wallet", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            History history = new History();
            history.setTimestamp(res.getString(res.getColumnIndex("timestamp")));
            history.setValue(res.getDouble(res.getColumnIndex("value")));
            wallet.getHistory7d().add(history);
            res.moveToNext();
        }

        return wallet;
    }


}
