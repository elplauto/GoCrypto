package fr.elplauto.gocrypto.model;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.utils.MyNumberFormatter;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private static final String TAG = "TransactionsAdapter";
    private OnTransactionClickListener OnTransactionClickListener;
    private List<Transaction> transactions = null;
    Map<Integer,Crypto> cryptoMap;

    public TransactionsAdapter(List<Transaction> transactions, OnTransactionClickListener OnTransactionClickListener, Map<Integer,Crypto> cryptoMap) {
        this.transactions = transactions;
        this.OnTransactionClickListener = OnTransactionClickListener;
        this.cryptoMap = cryptoMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.transaction_raw, parent, false);

        return new ViewHolder(itemView, OnTransactionClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        String cryptoSymbol = cryptoMap.get((transaction.getCryptoId())).getSymbol();
        if (transaction.getType().equals("sell")) {
            holder.sellSymbol.setText(cryptoSymbol);
            holder.buySymbol.setText("USD");
            holder.usdAmount.setTextColor(Color.GREEN);
        } else {
            holder.sellSymbol.setText("USD");
            holder.buySymbol.setText(cryptoSymbol);
            holder.usdAmount.setTextColor(Color.RED);
        }
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(transaction.getTimestamp());
        Instant i = Instant.from(ta);
        Date d = Date.from(i);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        String prettyDate = simpleDateFormat.format(d);

        holder.date.setText(prettyDate);
        String imgUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+ transaction.getCryptoId() +".png";
        Picasso.get().load(imgUrl).into(holder.cryptoLogo);
        holder.cryptoAmount.setText(MyNumberFormatter.formatNumber(transaction.getAmount()) + " " + cryptoSymbol);
        holder.usdAmount.setText(MyNumberFormatter.decimalPriceFormat(transaction.getUsd()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView sellSymbol;
        private final TextView buySymbol;
        private final TextView date;
        private final ImageView cryptoLogo;
        private final TextView cryptoAmount;
        private final TextView usdAmount;
        private OnTransactionClickListener OnTransactionClickListener;

        public ViewHolder(View view, OnTransactionClickListener OnTransactionClickListener) {
            super(view);
            sellSymbol = view.findViewById(R.id.sellSymbol);
            buySymbol = view.findViewById(R.id.buySymbol);
            date = view.findViewById(R.id.transaction_date);
            cryptoLogo = view.findViewById(R.id.cryptoLogo);
            cryptoAmount = view.findViewById(R.id.cryptoAmount);
            usdAmount = view.findViewById(R.id.usdAmount);
            this.OnTransactionClickListener = OnTransactionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnTransactionClickListener.onTransactionClick(getAdapterPosition());
        }
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(int position);
    }

}
