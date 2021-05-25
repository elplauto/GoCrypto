package fr.elplauto.gocrypto.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Comparator;
import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.CryptoDetailsService;
import fr.elplauto.gocrypto.api.LeaderboardService;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.Leaderboard;
import fr.elplauto.gocrypto.model.LeaderboardRaw;
import fr.elplauto.gocrypto.ui.trends.CryptoAdapter;
import fr.elplauto.gocrypto.utils.SessionManager;

public class LeaderboardFragment extends Fragment implements LeaderboardService.LeaderboardServiceCallbackListener, LeaderboardAdapter.OnUserClickListener {

    private LeaderboardViewModel leaderboardViewModel;
    private SwipeRefreshLayout swipeContainer;
    private List<LeaderboardRaw> leaderboardRaws;
    private RecyclerView recyclerView;
    LeaderboardService.LeaderboardServiceCallbackListener self = this;
    LeaderboardAdapter.OnUserClickListener onUserClickListener = this;
    SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        sessionManager = SessionManager.getInstance(getContext());

        recyclerView = root.findViewById(R.id.recycler_view_leaderboard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        swipeContainer = root.findViewById(R.id.swipeLeaderboard);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLeaderboard();
            }
        });

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                loadLeaderboard();
            }
        });
        
        return root;
    }

    @Override
    public void onLeaderboardServiceCallback(Leaderboard leaderboard) {
        this.leaderboardRaws = leaderboard.getRaws();
        this.leaderboardRaws.sort(new Comparator<LeaderboardRaw>() {
            @Override
            public int compare(LeaderboardRaw o1, LeaderboardRaw o2) {
                return o2.getUsd().compareTo(o1.getUsd());
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LeaderboardAdapter mAdapter = new LeaderboardAdapter(leaderboardRaws, onUserClickListener, sessionManager.getUsername());
                recyclerView.setAdapter(mAdapter);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    void loadLeaderboard() {
        LeaderboardService.getLeaderboard(getContext(), this);
    }

    @Override
    public void onUserClick(int position) {

    }
}