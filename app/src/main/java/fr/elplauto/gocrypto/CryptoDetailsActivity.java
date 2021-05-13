package fr.elplauto.gocrypto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;

import fr.elplauto.gocrypto.api.CryptoDetailsService;
import fr.elplauto.gocrypto.api.CryptoDetailsService.CryptoDetailsServiceCallbackListener;
import fr.elplauto.gocrypto.model.Crypto;

public class CryptoDetailsActivity extends AppCompatActivity implements CryptoDetailsServiceCallbackListener {

    private static final String TAG = "CryptoDetailsActivity";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_details);

        toolbar = findViewById(R.id.toolbar_crypto_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        int cryptoId = b.getInt("crypto_id");

        CryptoDetailsService.getCryptoDetails(this, cryptoId);
    }

    @Override
    public void onCryptoDetailsServiceCallback(final Crypto crypto) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(crypto.getName());
            }
        });

    }
}