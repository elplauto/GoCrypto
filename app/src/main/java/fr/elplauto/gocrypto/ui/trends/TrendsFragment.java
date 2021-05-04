package fr.elplauto.gocrypto.ui.trends;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.serviceCoinMarketCap.ServiceCoinMarketCap;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.SortCryptoCriteriaEnum;
import fr.elplauto.gocrypto.model.SortCryptoDirectionEnum;

public class TrendsFragment extends Fragment implements CryptoAdapter.OnCryptoClickListener, ServiceCoinMarketCap.CMCCallbackListener {

    private static final String TAG = "TrendsFragment";
    private DBManager dbManager;
    private TrendsViewModel trendsViewModel;
    private RecyclerView recyclerView;
    final CryptoAdapter.OnCryptoClickListener onCryptoClickListener = this;
    private SwipeRefreshLayout swipeContainer;
    private List<Crypto> cryptoList;
    SearchView searchView;
    ImageView sortArrowImageView;
    TextView displayPercentTextView;
    TextView allCryptoTextView;
    TextView sortTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trendsViewModel = ViewModelProviders.of(this).get(TrendsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_trends, container, false);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        recyclerView = root.findViewById(R.id.recycler_view_trends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        // recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        /*trendsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        swipeContainer = root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCryptoFromCMC();
            }
        });

        sortTextView = root.findViewById(R.id.sortTextView);
        sortTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_action_sheet_sort_crypto, (LinearLayout)root.findViewById(R.id.bottomSheetContainer));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                LinearLayout percentChangeLinearLayout = bottomSheetDialog.findViewById(R.id.percentChangeLinearLayout);
                percentChangeLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateSortPreferences(SortCryptoCriteriaEnum.PERCENT_CHANGE);
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                LinearLayout priceLinearLayout = bottomSheetDialog.findViewById(R.id.priceLinearLayout);
                priceLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateSortPreferences(SortCryptoCriteriaEnum.PRICE);
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                LinearLayout nameLinearLayout = bottomSheetDialog.findViewById(R.id.nameLinearLayout);
                nameLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateSortPreferences(SortCryptoCriteriaEnum.NAME);
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
        updateSortTextView();

        sortArrowImageView = root.findViewById(R.id.sortArrowImageView);
        sortArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseSortingDirection();
            }
        });
        updateSortArrowImage();

        displayPercentTextView = root.findViewById(R.id.displayPercentTextView);
        allCryptoTextView = root.findViewById(R.id.allCryptoTextView);

        dbManager = new DBManager(getContext());
        loadCryptoFromDB();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trends, menu);
        super.onCreateOptionsMenu(menu,inflater);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displayCryptoList(applyAllFiltersToList(cryptoList));
                return false;
            }
        });

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            }
        }

    }

    private void displayCryptoList(List<Crypto> cryptoList) {
        CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCryptoFromCMC() {
        ServiceCoinMarketCap.loadAllCrypto(getContext(), this);
    }

    private void loadCryptoFromDB() {
        cryptoList = dbManager.getAllCrypto();
        List<Crypto> filteredCryptoList = applyAllFiltersToList(cryptoList);
        displayCryptoList(filteredCryptoList);
    }

    @Override
    public void onCryptoClick(int position) {

    }


    @Override
    public void onCMCCallback(final List<Crypto> cryptoList) {
        this.cryptoList = cryptoList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Crypto> filteredList = applyAllFiltersToList(cryptoList);
                displayCryptoList(filteredList);
                swipeContainer.setRefreshing(false);
                dbManager.replaceCryptoList(cryptoList);
            }
        });
    }

    private List<Crypto> sortCryptoList(List<Crypto> cryptoList) {

        // get sorting preferences
        int sortCriteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
        SortCryptoCriteriaEnum criteria = SortCryptoCriteriaEnum.valueOf(sortCriteria);
        int sortDirection = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortDirection", 1);
        SortCryptoDirectionEnum direction = SortCryptoDirectionEnum.valueOf(sortDirection);

        if (criteria == SortCryptoCriteriaEnum.NAME) {
            if (direction == SortCryptoDirectionEnum.ASC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
            if (direction == SortCryptoDirectionEnum.DESC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                });
            }
        } else if (criteria == SortCryptoCriteriaEnum.PRICE) {
            if (direction == SortCryptoDirectionEnum.ASC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        Double diff = (o2.getPrice() - o1.getPrice());
                        return diff.compareTo(0d);
                    }
                });
            }
            if (direction == SortCryptoDirectionEnum.DESC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        Double diff = (o1.getPrice() - o2.getPrice());
                        return diff.compareTo(0d);
                    }
                });
            }
        } else if (criteria == SortCryptoCriteriaEnum.PERCENT_CHANGE) {
            if (direction == SortCryptoDirectionEnum.ASC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        Double diff = (o2.getPercentChange1h() - o1.getPercentChange1h());
                        return diff.compareTo(0d);
                    }
                });
            }
            if (direction == SortCryptoDirectionEnum.DESC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        Double diff = (o1.getPercentChange1h() - o2.getPercentChange1h());
                        return diff.compareTo(0d);
                    }
                });
            }
        }
        return cryptoList;
    }

    private void updateSortPreferences(SortCryptoCriteriaEnum newCriteriaEnum) {

        // get sorting preferences
        int oldCriteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
        SortCryptoCriteriaEnum oldCriteriaEnum = SortCryptoCriteriaEnum.valueOf(oldCriteria);
        int oldDirection = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortDirection", 1);
        SortCryptoDirectionEnum oldDirectionEnum = SortCryptoDirectionEnum.valueOf(oldDirection);
        SortCryptoDirectionEnum newDirectionEnum;

        if (newCriteriaEnum == oldCriteriaEnum) {
            if (oldDirectionEnum == SortCryptoDirectionEnum.ASC) {
                newDirectionEnum = SortCryptoDirectionEnum.DESC;
            } else {
                newDirectionEnum = SortCryptoDirectionEnum.ASC;
            }
        } else {
            newDirectionEnum = SortCryptoDirectionEnum.ASC;
        }

        SharedPreferences.Editor editor = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putInt("sortCriteria", newCriteriaEnum.getValue());
        editor.putInt("sortDirection", newDirectionEnum.getValue());
        editor.apply();

        updateSortArrowImage();
        updateSortTextView();
    }

    private List<Crypto> applyAllFiltersToList(List<Crypto> cryptoList) {
        String filter = searchView != null ? searchView.getQuery().toString() : "";
        List<Crypto> filteredList = filterCryptoListWithSearch(cryptoList, filter);
        List<Crypto> sortedList = sortCryptoList(filteredList);
        return sortedList;
    }

    private List<Crypto> filterCryptoListWithSearch(List<Crypto> cryptoList, String filter) {
        List<Crypto> filteredList = new ArrayList<>();
        for (Crypto crypto : cryptoList) {
            if (crypto.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(crypto);
            }
        }
        return filteredList;
    }

    private void reverseSortingDirection() {
        int criteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
        SortCryptoCriteriaEnum criteriaEnum = SortCryptoCriteriaEnum.valueOf(criteria);
        updateSortPreferences(criteriaEnum);
        displayCryptoList(applyAllFiltersToList(cryptoList));
    }

    private void updateSortArrowImage() {
        int direction = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortDirection", 1);
        int arrowId = direction == SortCryptoDirectionEnum.ASC.getValue() ? R.drawable.baseline_arrow_downward_24 : R.drawable.baseline_arrow_upward_24;
        sortArrowImageView.setImageResource(arrowId);
    }

    private void updateSortTextView() {
        String text = "";
        int criteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
        if (criteria == SortCryptoCriteriaEnum.NAME.getValue()) {
            text = "Sort by Name";
        } else if (criteria == SortCryptoCriteriaEnum.PRICE.getValue()) {
            text = "Sort by Price";
        } else if (criteria == SortCryptoCriteriaEnum.PERCENT_CHANGE.getValue()) {
            text = "Sort by %";
        }
        sortTextView.setText(text);
    }
}