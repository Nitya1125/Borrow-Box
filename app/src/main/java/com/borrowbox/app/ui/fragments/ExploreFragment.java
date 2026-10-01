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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.ItemGridAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.ui.activities.ItemDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvGrid;
    private ItemGridAdapter adapter;
    private View layoutEmpty;

    private TextView chipNearest, chipRecent, chipPopular, chipAvailable;
    private String currentSort = "Nearest";
    private List<Item> currentItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        etSearch = view.findViewById(R.id.etExploreSearch);
        rvGrid = view.findViewById(R.id.rvExploreGrid);
        layoutEmpty = view.findViewById(R.id.layoutExploreEmpty);

        chipNearest = view.findViewById(R.id.chipNearest);
        chipRecent = view.findViewById(R.id.chipRecent);
        chipPopular = view.findViewById(R.id.chipPopular);
        chipAvailable = view.findViewById(R.id.chipAvailable);

        setupGrid();
        setupSearch();
        setupSortChips();

        view.findViewById(R.id.btnExploreFilter).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filters applied: All active campus radius", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setupGrid() {
        currentItems = new ArrayList<>(DataManager.getInstance().getItems());
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
                filterAndSort(s.toString());
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
            filterAndSort(etSearch.getText().toString());
        };

        chipNearest.setOnClickListener(listener);
        chipRecent.setOnClickListener(listener);
        chipPopular.setOnClickListener(listener);
        chipAvailable.setOnClickListener(listener);
    }

    private void resetChips() {
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.text_secondary);
        chipNearest.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipNearest.setTextColor(unselectedColor);
        chipRecent.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipRecent.setTextColor(unselectedColor);
        chipPopular.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipPopular.setTextColor(unselectedColor);
        chipAvailable.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipAvailable.setTextColor(unselectedColor);
    }

    private void filterAndSort(String query) {
        List<Item> baseList = query.isEmpty() ?
                DataManager.getInstance().getItems() :
                DataManager.getInstance().searchItems(query);

        List<Item> result = new ArrayList<>(baseList);

        if ("Available".equalsIgnoreCase(currentSort)) {
            List<Item> availOnly = new ArrayList<>();
            for (Item it : result) {
                if (it.isAvailable()) availOnly.add(it);
            }
            result = availOnly;
        } else if ("Recent".equalsIgnoreCase(currentSort)) {
            Collections.reverse(result);
        }

        adapter.updateList(result);

        if (result.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvGrid.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvGrid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (etSearch != null) {
            filterAndSort(etSearch.getText().toString());
        } else {
            currentItems = new ArrayList<>(DataManager.getInstance().getItems());
            if (adapter != null) {
                adapter.updateList(currentItems);
            }
        }
    }
}
