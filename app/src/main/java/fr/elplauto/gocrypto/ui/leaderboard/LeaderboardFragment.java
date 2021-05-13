package fr.elplauto.gocrypto.ui.leaderboard;

import android.os.Bundle;
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
import fr.elplauto.gocrypto.api.LeaderboardService;
import fr.elplauto.gocrypto.model.Leaderboard;

public class LeaderboardFragment extends Fragment implements LeaderboardService.LeaderboardServiceCallbackListener {

    private LeaderboardViewModel leaderboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        
        return root;
    }

    @Override
    public void onLeaderboardServiceCallback(Leaderboard leaderboard) {

    }
}