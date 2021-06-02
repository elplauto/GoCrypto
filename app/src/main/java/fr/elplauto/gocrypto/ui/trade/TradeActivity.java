package fr.elplauto.gocrypto.ui.trade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import fr.elplauto.gocrypto.MainActivity;
import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.CryptoDetailsService;
import fr.elplauto.gocrypto.api.CryptoTradeService;
import fr.elplauto.gocrypto.api.WalletService;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.TradeResult;
import fr.elplauto.gocrypto.model.Wallet;
import fr.elplauto.gocrypto.utils.InputFilterMinMax;
import fr.elplauto.gocrypto.utils.MyNumberFormatter;
import fr.elplauto.gocrypto.utils.SessionManager;

public class TradeActivity extends AppCompatActivity implements CryptoDetailsService.CryptoDetailsServiceCallbackListener, WalletService.WalletServiceCallbackListener, CryptoTradeService.CryptoTradeServiceCallbackListener {

    private Toolbar toolbar;
    private Crypto crypto;
    private Wallet wallet;
    private int cryptoId;
    private CryptoDetailsService.CryptoDetailsServiceCallbackListener cryptoServiceListener= this;
    private WalletService.WalletServiceCallbackListener walletServiceListener = this;
    private CryptoTradeService.CryptoTradeServiceCallbackListener tradeServiceListener = this;
    private SessionManager sessionManager;
    private DBManager dbManager;
    private Timer timer;
    private Boolean buy = true;
    private Boolean usdInput = false;
    private Double availableCrypto;
    private Double maxAvailable;

    private TextView textViewSwitchTrade;
    private TextView availableTextView;
    private TextView symbolToUse;
    private TextView conversion;
    private TextView symbol;
    private TextView maxBtn;
    private Button buyOrSellBtn;
    private EditText selectedAmount;
    private ImageView imgCrypto;
    private ImageView switchInputSymbol;
    private TextView convertedAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        dbManager = new DBManager(getApplicationContext());

        WalletService.getWallet(getApplicationContext(), walletServiceListener, sessionManager.getUsername());

        toolbar = findViewById(R.id.toolbar_trade);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewSwitchTrade = findViewById(R.id.switchTrade);
        textViewSwitchTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy = !buy;
                updateAllDisplay();
            }
        });
        symbolToUse = findViewById(R.id.symbolToUse);

        conversion = findViewById(R.id.conversion);
        imgCrypto = findViewById(R.id.imgCrypto);
        buyOrSellBtn = findViewById(R.id.buyOrSellBtn);
        symbol = findViewById(R.id.symbol);
        selectedAmount = findViewById(R.id.selectedAmount);
        maxBtn = findViewById(R.id.maxBtn);
        availableTextView = findViewById(R.id.availableAmount);
        switchInputSymbol = findViewById(R.id.switchInputSymbol);
        convertedAmount = findViewById(R.id.convertedAmount);

        switchInputSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usdInput = !usdInput;
                selectedAmount.setText("0");
                updateAllDisplay();
            }
        });

        maxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAmount.setText(maxAvailable.toString());
            }
        });

        selectedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               updateAllDisplay();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buyOrSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double finalAmount = Double.parseDouble(selectedAmount.getText().toString());
                if (usdInput) {
                    finalAmount = finalAmount / crypto.getPrice();
                }
                if (buy) {
                    CryptoTradeService.buyCrypto(getApplicationContext(), tradeServiceListener, sessionManager.getUsername(), cryptoId, finalAmount);
                } else {
                    CryptoTradeService.sellCrypto(getApplicationContext(), tradeServiceListener, sessionManager.getUsername(), cryptoId, finalAmount);
                }
            }
        });

        Bundle b = getIntent().getExtras();
        cryptoId = b.getInt("crypto_id");
    }

    @Override
    public void onCryptoDetailsServiceCallback(final Crypto crypto) {
        this.crypto = crypto;
        final String imgUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+ crypto.getId() +".png";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(imgUrl).into(imgCrypto);
                String unitPrice = MyNumberFormatter.decimalPriceFormat(crypto.getPrice());
                conversion.setText(1 + crypto.getSymbol() + " = " + unitPrice);
                symbol.setText(crypto.getSymbol());
                updateAllDisplay();
            }
        });
    }

    @Override
    public void onWalletServiceCallback(Wallet wallet) {
        this.wallet = wallet;
        availableCrypto = 0d;
        for (CryptoInWallet cryptoInWallet : wallet.getCrypto()) {
            if (cryptoInWallet.getId() == cryptoId) {
                availableCrypto = cryptoInWallet.getAmount();
                break;
            }
        }
        final String cryptoSymbol = dbManager.getCryptoMap().get(cryptoId).getSymbol();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                availableTextView.setText("Available: " + MyNumberFormatter.formatNumber(availableCrypto) + " " + cryptoSymbol);
            }
        });
        
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CryptoDetailsService.getCryptoDetails(getApplicationContext(), cryptoServiceListener, cryptoId);
            }
        }, 0, 20*1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateTitle() {
        String title = buy ? "Buy " : "Sell ";
        title += crypto.getSymbol();
        final String finalTitle = title;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setTitle(finalTitle);
                buyOrSellBtn.setText(finalTitle);
            }
        });
    }

    private void updateAllDisplay() {
        updateTitle();
        String action = buy ? "Sell" : "Buy";
        textViewSwitchTrade.setText(action);

        if (usdInput) {
            symbol.setText("USD");
            symbolToUse.setText(crypto.getSymbol());
        } else {
            symbol.setText(crypto.getSymbol());
            symbolToUse.setText("USD");
        }

        String amount = selectedAmount.getText().toString();
        if (amount.length() == 0) {
            amount="0";
        }
        Double doubleAmount = Double.parseDouble(amount);

        boolean enableBtn = doubleAmount > 0d;
        buyOrSellBtn.setEnabled(enableBtn);

        String symb = "";
        Double nb = 0d;
        if (usdInput) {
            symb = "USD";
            if (buy) {
                nb = wallet.getUsd();
            } else {
                nb = availableCrypto * crypto.getPrice();
            }
        } else {
            symb = crypto.getSymbol();
            if (buy) {
                nb = wallet.getUsd() / crypto.getPrice();
            } else {
                nb = availableCrypto;
            }
        }
        availableTextView.setText("Available: " + MyNumberFormatter.formatNumber(nb) + " " + symb);
        maxAvailable = nb;
        selectedAmount.setFilters(new InputFilter[]{ new InputFilterMinMax(0, nb)});

        if(doubleAmount > maxAvailable) {
            selectedAmount.setText(maxAvailable.toString());
            doubleAmount = maxAvailable;
        }

        if (usdInput) {
            Double converted = doubleAmount / crypto.getPrice();
            convertedAmount.setText("= " + MyNumberFormatter.formatNumber(converted) + " " + crypto.getSymbol());
        } else {
            Double converted = crypto.getPrice() * doubleAmount;
            convertedAmount.setText("= " + MyNumberFormatter.formatNumber(converted) + " USD");
        }
    }

    @Override
    public void onCryptoTradeServiceCallback(final TradeResult tradeResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tradeResult.getSuccess()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Successful transaction !", Toast.LENGTH_LONG);
                    toast.getView().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    toast.show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error, please try again", Toast.LENGTH_LONG);
                    toast.getView().getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    toast.show();
                }
            }
        });

    }
}