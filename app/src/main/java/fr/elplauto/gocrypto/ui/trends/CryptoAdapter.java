package fr.elplauto.gocrypto.ui.trends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.elplauto.gocrypto.BuildConfig;
import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.searchAllCrypto.Crypto;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {

    private static final String TAG = "CryptoAdapter";
    private OnCryptoClickListener OnCryptoClickListener;
    private Context context;

    private List<Crypto> listCrypto = null;

    public CryptoAdapter(List<Crypto> listCrypto, OnCryptoClickListener OnCryptoClickListener, Context current) {
        this.listCrypto = listCrypto;
        this.OnCryptoClickListener = OnCryptoClickListener;
        this.context = current;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.crypto_raw, parent, false);

        return new ViewHolder(itemView, OnCryptoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Crypto crypto = listCrypto.get(position);
        holder.textViewCryptoName.setText(crypto.getName());
        holder.textViewCryptoShortName.setText(crypto.getSymbol());
        Double percentChange1h = crypto.getQuote().getUsd().getPercentChange1h();
        String progressionPercent = String.format("%.03f", Math.abs(percentChange1h)) + "%";
        holder.textViewProgressionPercent.setText(progressionPercent);
        String priceInDollar = formatPrice(crypto.getQuote().getUsd().getPrice());
        holder.textViewPriceInDollar.setText(priceInDollar);

        String variableValue = "icon_crypto_" + crypto.getId();
        int resId = this.context.getResources().getIdentifier(variableValue, "drawable", BuildConfig.APPLICATION_ID);
        holder.imageViewIconCrypto.setImageResource(resId);

        if (percentChange1h >= 0) {
            holder.imageViewUpDown.setImageResource(R.drawable.up_arrow);
        } else {
            holder.imageViewUpDown.setImageResource(R.drawable.down_arrow);
        }

    }

    @Override
    public int getItemCount() {
        return listCrypto.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewCryptoName;
        private final TextView textViewCryptoShortName;
        private final TextView textViewProgressionPercent;
        private final TextView textViewPriceInDollar;
        private final ImageView imageViewIconCrypto;
        private final ImageView imageViewUpDown;

        private OnCryptoClickListener OnCryptoClickListener;

        public ViewHolder(View view, OnCryptoClickListener OnCryptoClickListener) {
            super(view);
            textViewCryptoName = view.findViewById(R.id.textViewCryptoName);
            textViewCryptoShortName = view.findViewById(R.id.textViewCryptoShortName);
            textViewProgressionPercent = view.findViewById(R.id.textViewProgressionPercent);
            textViewPriceInDollar = view.findViewById(R.id.textViewPriceInDollar);
            imageViewIconCrypto = view.findViewById(R.id.imageViewIconCrypto);
            imageViewUpDown = view.findViewById(R.id.imageViewUpDown);
            this.OnCryptoClickListener = OnCryptoClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnCryptoClickListener.onCryptoClick(getAdapterPosition());
        }
    }

    public interface OnCryptoClickListener {
        void onCryptoClick(int position);
    }

    private String formatPrice(Double price) {
        String priceFormatted = null;
        if (price < 1) {
            priceFormatted = String.format("%.04f", price);
        } else if (price < 10) {
            priceFormatted = String.format("%.03f", price);
        } else {
            priceFormatted = String.format("%.02f", price);
        }
        return "$" + priceFormatted;
    }
}
