package fr.elplauto.gocrypto.ui.trends;

import android.content.Context;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.elplauto.gocrypto.CryptoDetailsActivity;
import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.CryptoService;
import fr.elplauto.gocrypto.database.DBManager;
import fr.elplauto.gocrypto.model.Crypto;
import fr.elplauto.gocrypto.model.SortCryptoCriteriaEnum;
import fr.elplauto.gocrypto.model.SortCryptoDirectionEnum;

public class TrendsFragment extends Fragment implements CryptoAdapter.OnCryptoClickListener, CryptoService.CryptoServiceCallbackListener {

    private static final String TAG = "TrendsFragment";
    private DBManager dbManager;
    private TrendsViewModel trendsViewModel;
    private RecyclerView recyclerView;
    final CryptoAdapter.OnCryptoClickListener onCryptoClickListener = this;
    private SwipeRefreshLayout swipeContainer;
    private List<Crypto> cryptoList;
    private List<Crypto> displayedList;
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

                int sortCriteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
                int sortDirection = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortDirection", 1);

                int colorBlue = ContextCompat.getColor(getContext(), R.color.blue);
                int textToColor = 0, imgUp = 0, imgDown = 0;

                if (sortCriteria == SortCryptoCriteriaEnum.NAME.getValue()) {
                    textToColor = R.id.nameText;
                    imgUp = R.id.imgUpName;
                    imgDown = R.id.imgDownName;
                } else if (sortCriteria == SortCryptoCriteriaEnum.PRICE.getValue()) {
                    textToColor = R.id.priceText;
                    imgUp = R.id.imgUpPrice;
                    imgDown = R.id.imgDownPrice;
                } else if (sortCriteria == SortCryptoCriteriaEnum.PERCENT_CHANGE.getValue()) {
                    textToColor = R.id.percentChangeText;
                    imgUp = R.id.imgUpPercentChange;
                    imgDown = R.id.imgDownPercentChange;
                }

                ((TextView) bottomSheetView.findViewById(textToColor)).setTextColor(colorBlue);

                ImageView imgViewUp = bottomSheetView.findViewById(imgUp);
                ImageView imgViewDown = bottomSheetView.findViewById(imgDown);
                imgViewUp.setVisibility(View.VISIBLE);
                imgViewDown.setVisibility(View.VISIBLE);
                if (sortDirection == SortCryptoDirectionEnum.ASC.getValue()) {
                    imgViewDown.setColorFilter(colorBlue);
                } else {
                    imgViewUp.setColorFilter(colorBlue);
                }

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

                bottomSheetDialog.show();
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
        displayPercentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_action_sheet_choose_percentage, (LinearLayout)root.findViewById(R.id.bottomSheetContainer));
                bottomSheetDialog.setContentView(bottomSheetView);

                String percentChangePreference = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("percentChange", "1h");

                int colorBlue = ContextCompat.getColor(getContext(), R.color.blue);
                int textToColor = 0;
                switch (percentChangePreference) {
                    case "1h":
                        textToColor = R.id.percentChange1h;
                        break;
                    case "24h":
                        textToColor = R.id.percentChange24h;
                        break;
                    case "7d":
                        textToColor = R.id.percentChange7d;
                        break;
                    case "30d":
                        textToColor = R.id.percentChange30d;
                        break;
                    case "60d":
                        textToColor = R.id.percentChange60d;
                        break;
                    case "90d":
                        textToColor = R.id.percentChange90d;
                        break;
                }
                ((TextView) bottomSheetView.findViewById(textToColor)).setTextColor(colorBlue);

                TextView percentChange1h = bottomSheetDialog.findViewById(R.id.percentChange1h);
                percentChange1h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("1h");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                TextView percentChange24h = bottomSheetDialog.findViewById(R.id.percentChange24h);
                percentChange24h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("24h");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                TextView percentChange7d = bottomSheetDialog.findViewById(R.id.percentChange7d);
                percentChange7d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("7d");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                TextView percentChange30d = bottomSheetDialog.findViewById(R.id.percentChange30d);
                percentChange30d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("30d");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                TextView percentChange60d = bottomSheetDialog.findViewById(R.id.percentChange60d);
                percentChange60d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("60d");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });
                TextView percentChange90d = bottomSheetDialog.findViewById(R.id.percentChange90d);
                percentChange90d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePercentDisplayPreference("90d");
                        displayCryptoList(applyAllFiltersToList(cryptoList));
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();

            }
        });
        updatePercentChangeTextView();

        allCryptoTextView = root.findViewById(R.id.allCryptoTextView);

        dbManager = new DBManager(getContext());

        boolean dataLoaded = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean("dataLoaded", false);
        if (dataLoaded) {
            loadCryptoFromDB();
        } else {
            loadCryptoFromCMC();
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    loadCryptoFromCMC();
                }
            });
            SharedPreferences.Editor editor = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
            editor.putBoolean("dataLoaded", true);
            editor.commit();
        }


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
        this.displayedList = cryptoList;
        String percentChangePreference = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("percentChange", "1h");
        CryptoAdapter mAdapter = new CryptoAdapter(cryptoList, onCryptoClickListener, getContext(), percentChangePreference);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCryptoFromCMC() {
        CryptoService.loadAllCrypto(getContext(), this);
    }

    private void loadCryptoFromDB() {
        cryptoList = dbManager.getAllCrypto();
        List<Crypto> filteredCryptoList = applyAllFiltersToList(cryptoList);
        displayCryptoList(filteredCryptoList);
    }

    @Override
    public void onCryptoClick(int position) {
        Crypto crypto = this.displayedList.get(position);
        Intent intent = new Intent(getContext(), CryptoDetailsActivity.class);
        Bundle b = new Bundle();
        b.putInt("crypto_id", crypto.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private List<Crypto> sortCryptoList(List<Crypto> cryptoList) {

        // get sorting preferences
        int sortCriteria = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortCriteria", 1);
        SortCryptoCriteriaEnum criteria = SortCryptoCriteriaEnum.valueOf(sortCriteria);
        int sortDirection = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getInt("sortDirection", 1);
        SortCryptoDirectionEnum direction = SortCryptoDirectionEnum.valueOf(sortDirection);
        final String percentChangePreference = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("percentChange", "1h");

        if (criteria == SortCryptoCriteriaEnum.NAME) {
            if (direction == SortCryptoDirectionEnum.ASC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                });
            }
            if (direction == SortCryptoDirectionEnum.DESC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        return o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase());
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
                        Double diff = (o2.getPercentChange(percentChangePreference) - o1.getPercentChange(percentChangePreference));
                        return diff.compareTo(0d);
                    }
                });
            }
            if (direction == SortCryptoDirectionEnum.DESC) {
                cryptoList.sort(new Comparator<Crypto>() {
                    @Override
                    public int compare(Crypto o1, Crypto o2) {
                        Double diff = (o1.getPercentChange(percentChangePreference) - o2.getPercentChange(percentChangePreference));
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
            if (crypto.getName().toLowerCase().contains(filter.toLowerCase())
                    || crypto.getSymbol().toLowerCase().contains(filter.toLowerCase())) {
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
        sortArrowImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
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

    private void updatePercentDisplayPreference(String newPercentChange) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString("percentChange", newPercentChange);
        editor.apply();
        updatePercentChangeTextView();
    }

    private void updatePercentChangeTextView() {
        String text = "";
        String percentChangePreference = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("percentChange", "1h");
        switch (percentChangePreference) {
            case "1h":
                text = "%(1h)";
                break;
            case "24h":
                text = "%(24h)";
                break;
            case "7d":
                text = "%(7d)";
                break;
            case "30d":
                text = "%(30d)";
                break;
            case "60d":
                text = "%(60d)";
                break;
            case "90d":
                text = "%(90d)";
                break;
        }
        displayPercentTextView.setText(text);
    }

    @Override
    public void onCryptoServiceCallback(final List<Crypto> cryptoList) {
        this.cryptoList = cryptoList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dbManager.replaceCryptoList(cryptoList);
                List<Crypto> filteredList = applyAllFiltersToList(cryptoList);
                displayCryptoList(filteredList);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}