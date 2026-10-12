package com.borrowbox.app.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.ItemGridAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.ItemDetailsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvGrid;
    private ItemGridAdapter adapter;
    private View layoutEmpty;
    private ProgressBar progressBar;

    private TextView chipNearest, chipRecent, chipPopular, chipAvailable;
    private String currentSort = "Recent";
    private final List<Item> currentItems = new ArrayList<>();
    private ItemRepository itemRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        itemRepository = ItemRepository.getInstance(requireContext());

        etSearch = view.findViewById(R.id.etExploreSearch);
        rvGrid = view.findViewById(R.id.rvExploreGrid);
        layoutEmpty = view.findViewById(R.id.layoutExploreEmpty);
        progressBar = view.findViewById(R.id.progressExploreLoading);

        chipNearest = view.findViewById(R.id.chipNearest);
        chipRecent = view.findViewById(R.id.chipRecent);
        chipPopular = view.findViewById(R.id.chipPopular);
        chipAvailable = view.findViewById(R.id.chipAvailable);

        setupGrid();
        setupSearch();
        setupSortChips();

        View btnFilter = view.findViewById(R.id.btnExploreFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                Snackbar.make(view, R.string.all_campus_active_filter, Snackbar.LENGTH_SHORT).show();
            });
        }

        return view;
    }

    private void setupGrid() {
        adapter = new ItemGridAdapter(getContext(), currentItems, item -> {
            Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });

        rvGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvGrid.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSortChips() {
        View.OnClickListener listener = v -> {
            resetChips();
            TextView tv = (TextView) v;
            tv.setBackgroundResource(R.drawable.bg_chip_selected);
            tv.setTextColor(Color.WHITE);
            currentSort = tv.getText().toString();
            loadData(etSearch.getText().toString().trim());
        };

        if (chipNearest != null) chipNearest.setOnClickListener(listener);
        if (chipRecent != null) chipRecent.setOnClickListener(listener);
        if (chipPopular != null) chipPopular.setOnClickListener(listener);
        if (chipAvailable != null) chipAvailable.setOnClickListener(listener);
    }

    private void resetChips() {
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.text_secondary);
        if (chipNearest != null) {
            chipNearest.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipNearest.setTextColor(unselectedColor);
        }
        if (chipRecent != null) {
            chipRecent.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipRecent.setTextColor(unselectedColor);
        }
        if (chipPopular != null) {
            chipPopular.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipPopular.setTextColor(unselectedColor);
        }
        if (chipAvailable != null) {
            chipAvailable.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipAvailable.setTextColor(unselectedColor);
        }
    }

    private void loadData(String query) {
        User user = DataManager.getInstance().getCurrentUser();
        String currentUserId = user != null ? user.getId() : "";

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RepositoryCallback<List<Item>> callback = new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                List<Item> result = new ArrayList<>(items);
                if ("Available".equalsIgnoreCase(currentSort)) {
                    List<Item> avail = new ArrayList<>();
                    for (Item it : result) {
                        if (it.isAvailable()) avail.add(it);
                    }
                    result = avail;
                } else if ("Popular".equalsIgnoreCase(currentSort)) {
                    // Sort by rating desc
                    Collections.sort(result, (a, b) -> Double.compare(b.getOwnerRating(), a.getOwnerRating()));
                }

                currentItems.clear();
                currentItems.addAll(result);
                adapter.notifyDataSetChanged();

                if (currentItems.isEmpty()) {
                    if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                    rvGrid.setVisibility(View.GONE);
                } else {
                    if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                    rvGrid.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                rvGrid.setVisibility(View.GONE);
            }
        };

        if (query.isEmpty()) {
            itemRepository.getRecommendedItems(currentUserId, callback);
        } else {
            itemRepository.searchItems(query, currentUserId, callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (etSearch != null) {
            loadData(etSearch.getText().toString().trim());
        }
    }
}
