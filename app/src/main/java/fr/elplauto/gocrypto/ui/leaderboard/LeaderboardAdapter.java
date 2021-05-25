package fr.elplauto.gocrypto.ui.leaderboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.LeaderboardRaw;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private static final String TAG = "LeaderboardAdapter";
    private OnUserClickListener OnUserClickListener;
    private List<LeaderboardRaw> leaderboardRaws = null;
    private String username;

    public LeaderboardAdapter(List<LeaderboardRaw> leaderboardRaws, OnUserClickListener OnUserClickListener, String username) {
        this.leaderboardRaws = leaderboardRaws;
        this.OnUserClickListener = OnUserClickListener;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.leaderboard_raw, parent, false);

        return new ViewHolder(itemView, OnUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardRaw raw = leaderboardRaws.get(position);
        holder.textViewRank.setText(String.valueOf(position + 1) + ".");
        holder.textViewUsername.setText(raw.getUsername());
        String priceInDollar = formatPrice(raw.getUsd());
        holder.textViewUsd.setText(priceInDollar);

        if (raw.getUsername().equals(username)) {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#1ec734"));
        }
    }

    @Override
    public int getItemCount() {
        return leaderboardRaws.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewRank;
        private final TextView textViewUsername;
        private final TextView textViewUsd;
        private LinearLayout linearLayout;
        private OnUserClickListener OnUserClickListener;

        public ViewHolder(View view, OnUserClickListener OnUserClickListener) {
            super(view);
            textViewRank = view.findViewById(R.id.ldb_rank);
            textViewUsername = view.findViewById(R.id.ldb_username);
            textViewUsd = view.findViewById(R.id.ldb_usd);
            linearLayout = view.findViewById(R.id.ldb_raw);
            this.OnUserClickListener = OnUserClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnUserClickListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
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
