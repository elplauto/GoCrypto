package fr.elplauto.gocrypto.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.WalletService;
import fr.elplauto.gocrypto.model.Wallet;
import fr.elplauto.gocrypto.utils.SessionManager;

public class AccountFragment extends Fragment implements WalletService.WalletServiceCallbackListener{

    TextView textViewUSD;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);

        final SessionManager sessionManager = SessionManager.getInstance(getContext());

        TextView usernameText = root.findViewById(R.id.usernameAccount);
        usernameText.setText(sessionManager.getUsername());

        textViewUSD = root.findViewById(R.id.account_usd);

        Button logoutBtn = root.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                sessionManager.checkLogin();
                Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_LONG).show();
            }
        });

        final String username = sessionManager.getUsername();
        WalletService.getWallet(getContext(), this, username);

        return root;
    }

    @Override
    public void onWalletServiceCallback(final Wallet wallet) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = formatPrice(wallet.getHistory1h().get(0).getValue());
                textViewUSD.setText(text);
            }
        });
    }

    private String formatPrice(Double price) {
        int fractionDigits = 0;
        double inv = 1d / price;
        while (inv > 0.1) {
            inv = inv / 10d;
            fractionDigits = fractionDigits + 1;
        }
        fractionDigits += 2;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setCurrency(Currency.getInstance("USD"));
        format.setMinimumFractionDigits(fractionDigits);
        format.setMaximumFractionDigits(fractionDigits);
        return format.format(price);
    }
}