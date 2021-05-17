package fr.elplauto.gocrypto.ui.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.CryptoMerge;

public class CryptoWalletAdapter extends RecyclerView.Adapter<CryptoWalletAdapter.ViewHolder> {

    private static final String TAG = "CryptoWalletAdapter";
    private OnCryptoWalletClickListener OnCryptoWalletClickListener;
    private Context context;
    private List<CryptoMerge> cryptoList = null;

    public CryptoWalletAdapter(List<CryptoMerge> cryptoList, OnCryptoWalletClickListener OnCryptoWalletClickListener, Context current) {
        this.cryptoList = cryptoList;
        this.OnCryptoWalletClickListener = OnCryptoWalletClickListener;
        this.context = current;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.crypto_raw_wallet, parent, false);

        return new ViewHolder(itemView, OnCryptoWalletClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CryptoMerge cryptoMerge = cryptoList.get(position);
        holder.textViewCryptoName.setText(cryptoMerge.getCrypto().getName());
        holder.textViewCryptoShortName.setText(cryptoMerge.getCrypto().getSymbol());
        String unitPrice = formatPrice(cryptoMerge.getCrypto().getPrice());
        holder.textViewUnitPrice.setText(unitPrice);
        holder.textViewAmount.setText(cryptoMerge.getCryptoInWallet().getAmount().toString());

        String imgUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+ cryptoMerge.getCrypto().getId() +".png";
        Picasso.get().load(imgUrl).into(holder.imageViewIconCrypto);
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewCryptoName;
        private final TextView textViewCryptoShortName;
        private final TextView textViewAmount;
        private final TextView textViewUnitPrice;
        private final ImageView imageViewIconCrypto;

        private OnCryptoWalletClickListener OnCryptoWalletClickListener;

        public ViewHolder(View view, OnCryptoWalletClickListener OnCryptoWalletClickListener) {
            super(view);
            textViewCryptoName = view.findViewById(R.id.textViewCryptoName);
            textViewCryptoShortName = view.findViewById(R.id.textViewCryptoShortName);
            textViewAmount = view.findViewById(R.id.textViewAmount);
            textViewUnitPrice = view.findViewById(R.id.textViewUnitPrice);
            imageViewIconCrypto = view.findViewById(R.id.imageViewIconCrypto);
            this.OnCryptoWalletClickListener = OnCryptoWalletClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnCryptoWalletClickListener.onCryptoClick(getAdapterPosition());
        }
    }

    public interface OnCryptoWalletClickListener {
        void onCryptoClick(int position);
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
