package fr.elplauto.gocrypto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        db.execSQL("DROP TABLE IF EXISTS " + CRYPTO_TABLE_NAME);
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
        db.execSQL("delete from crypto");
    }

}
