package fr.elplauto.gocrypto.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.model.SessionManager;
import fr.elplauto.gocrypto.ui.leaderboard.LeaderboardViewModel;

public class AccountFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);

        final SessionManager sessionManager = SessionManager.getInstance(getContext());

        Button logoutBtn = root.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                sessionManager.checkLogin();
                Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}