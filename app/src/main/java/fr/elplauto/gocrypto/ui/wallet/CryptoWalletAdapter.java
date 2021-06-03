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

import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.CryptoMerge;
import fr.elplauto.gocrypto.utils.MyNumberFormatter;

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
        String amountInUsd = MyNumberFormatter.decimalPriceFormat(cryptoMerge.getCrypto().getPrice() * cryptoMerge.getCryptoInWallet().getAmount());
        holder.textViewAmountInUsd.setText(amountInUsd);
        String amount = MyNumberFormatter.formatNumber(cryptoMerge.getCryptoInWallet().getAmount());
        holder.textViewAmount.setText(amount);

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
        private final TextView textViewAmountInUsd;
        private final ImageView imageViewIconCrypto;

        private OnCryptoWalletClickListener OnCryptoWalletClickListener;

        public ViewHolder(View view, OnCryptoWalletClickListener OnCryptoWalletClickListener) {
            super(view);
            textViewCryptoName = view.findViewById(R.id.textViewCryptoName);
            textViewCryptoShortName = view.findViewById(R.id.textViewCryptoShortName);
            textViewAmount = view.findViewById(R.id.textViewAmount);
            textViewAmountInUsd = view.findViewById(R.id.textViewAmountInUsd);
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

}
