package fr.elplauto.gocrypto.ui.trends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
        holder.textViewTitle.setText(crypto.getName());
        holder.textViewTitleSecondary.setText(crypto.getQuote().getUsd().getPrice().toString());

        String variableValue = "icon_crypto_" + crypto.getId();
        int resId = this.context.getResources().getIdentifier(variableValue, "drawable", BuildConfig.APPLICATION_ID);
        holder.imageView.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return listCrypto.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewTitleSecondary;
        private final ImageView imageView;

        private OnCryptoClickListener OnCryptoClickListener;

        public ViewHolder(View view, OnCryptoClickListener OnCryptoClickListener) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewTitleSecondary = view.findViewById(R.id.textViewTitleSecondary);
            imageView = view.findViewById(R.id.imageView);
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
}
