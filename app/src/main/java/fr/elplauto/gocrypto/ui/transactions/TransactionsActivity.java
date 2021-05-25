package fr.elplauto.gocrypto.ui.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Transaction;
import fr.elplauto.gocrypto.model.Wallet;

public class TransactionsActivity extends AppCompatActivity implements TransactionsAdapter.OnTransactionClickListener{

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeContainer;
    private List<Transaction> transactions;
    private RecyclerView recyclerView;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        dbManager = new DBManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar_transactions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Wallet wallet = (Wallet) getIntent().getSerializableExtra("wallet");

        recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        TransactionsAdapter  mAdapter = new TransactionsAdapter(wallet.getTransactions(), this, dbManager.getCryptoMap());
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTransactionClick(int position) {

    }
}