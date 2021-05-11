package fr.elplauto.gocrypto.ui.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.CryptoInWallet;
import fr.elplauto.gocrypto.model.History;
import fr.elplauto.gocrypto.model.Wallet;

public class WalletFragment extends Fragment {

    private static final String TAG = "WalletFragment";
    private WalletViewModel walletViewModel;
    private DBManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        walletViewModel =
                ViewModelProviders.of(this).get(WalletViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        final TextView textView = root.findViewById(R.id.text_wallet);
        walletViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        dbManager = new DBManager(getContext());
        Wallet wallet = new Wallet(351.23);
        wallet.getHistory().add(new History("lalala", 985.100));
        wallet.getHistory().add(new History("papapa", 1000.100));
        wallet.getCrypto().add(new CryptoInWallet(1, 468.2, 453.3));
        wallet.getCrypto().add(new CryptoInWallet(2, 468.2, 453.3));
        dbManager.saveWallet(wallet);
        Wallet test = dbManager.getWallet();
        Log.d(TAG, wallet.getUsd()+"");

        return root;
    }
}