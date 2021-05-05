package fr.elplauto.gocrypto.ui.trends;

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
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.searchAllCrypto.DataSearchCrypto;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {

    private static final String TAG = "CryptoAdapter";
    private OnCryptoClickListener OnCryptoClickListener;
    private Context context;
    private String percentChangePreference;
    private List<Crypto> cryptoList = null;

    public CryptoAdapter(List<Crypto> cryptoList, OnCryptoClickListener OnCryptoClickListener, Context current, String percentChangePreference) {
        this.cryptoList = cryptoList;
        this.OnCryptoClickListener = OnCryptoClickListener;
        this.context = current;
        this.percentChangePreference = percentChangePreference;
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
        Crypto crypto = cryptoList.get(position);
        holder.textViewCryptoName.setText(crypto.getName());
        holder.textViewCryptoShortName.setText(crypto.getSymbol());

        Double percentChange1h = crypto.getPercentChange(this.percentChangePreference);
        String progressionPercent = String.format("%.02f", Math.abs(percentChange1h)) + "%";
        holder.textViewProgressionPercent.setText(progressionPercent);
        String priceInDollar = formatPrice(crypto.getPrice());
        holder.textViewPriceInDollar.setText(priceInDollar);

        String imgUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+ crypto.getId() +".png";
        Picasso.get().load(imgUrl).into(holder.imageViewIconCrypto);

        if (percentChange1h >= 0) {
            holder.imageViewUpDown.setImageResource(R.drawable.up_arrow);
        } else {
            holder.imageViewUpDown.setImageResource(R.drawable.down_arrow);
        }

    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
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
