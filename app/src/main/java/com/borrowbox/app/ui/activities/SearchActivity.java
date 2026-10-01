package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.SearchResultAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageButton btnBack, btnClearSearch;
    private EditText etSearch;
    private LinearLayout layoutRecentSearches;
    private LinearLayout layoutRecentList;
    private TextView btnClearAllRecents;
    private GridLayout gridCategories;
    private RecyclerView rvSearchResults;
    private SearchResultAdapter resultAdapter;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnBack = findViewById(R.id.btnSearchBack);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        etSearch = findViewById(R.id.etSearchInput);
        layoutRecentSearches = findViewById(R.id.layoutRecentSearches);
        layoutRecentList = findViewById(R.id.layoutRecentList);
        btnClearAllRecents = findViewById(R.id.btnClearAllRecents);
        gridCategories = findViewById(R.id.gridCategories);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        layoutEmpty = findViewById(R.id.layoutSearchEmpty);

        btnBack.setOnClickListener(v -> finish());
        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));

        setupRecentSearches();
        setupPopularCategories();
        setupSearchListener();
        setupRecyclerView();
    }

    private void setupRecentSearches() {
        layoutRecentList.removeAllViews();
        List<String> recents = DataManager.getInstance().getRecentSearches();

        if (recents == null || recents.isEmpty()) {
            layoutRecentSearches.setVisibility(View.GONE);
            return;
        }

        layoutRecentSearches.setVisibility(View.VISIBLE);

        for (String term : recents) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 20, 0, 20);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            iv.setImageResource(R.drawable.ic_search);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary));
            row.addView(iv);

            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            lp.setMargins(20, 0, 0, 0);
            tv.setLayoutParams(lp);
            tv.setText(term);
            tv.setTextSize(14f);
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            row.addView(tv);

            row.setOnClickListener(v -> {
                etSearch.setText(term);
                etSearch.setSelection(term.length());
            });

            layoutRecentList.addView(row);
        }

        btnClearAllRecents.setOnClickListener(v -> {
            DataManager.getInstance().clearRecentSearches();
            layoutRecentSearches.setVisibility(View.GONE);
            layoutRecentList.removeAllViews();
        });
    }

    private void setupPopularCategories() {
        gridCategories.removeAllViews();
        List<Category> categories = DataManager.getInstance().getCategories();
        for (Category cat : categories) {
            if ("all".equalsIgnoreCase(cat.getId())) continue;

            TextView catButton = new TextView(this);
            catButton.setText("  " + cat.getLabel());
            catButton.setTextSize(13f);
            catButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            catButton.setBackgroundResource(R.drawable.bg_card);
            catButton.setPadding(28, 24, 28, 24);

            Drawable icon = ContextCompat.getDrawable(this, cat.getIconRes());
            if (icon != null) {
                icon.setBounds(0, 0, 44, 44);
                icon.setTint(ContextCompat.getColor(this, R.color.primary));
                catButton.setCompoundDrawables(icon, null, null, null);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            catButton.setLayoutParams(params);

            catButton.setOnClickListener(v -> {
                etSearch.setText(cat.getLabel());
                etSearch.setSelection(cat.getLabel().length());
            });

            gridCategories.addView(catButton);
        }
    }

    private void setupRecyclerView() {
        resultAdapter = new SearchResultAdapter(this, new ArrayList<>(), item -> {
            DataManager.getInstance().addRecentSearch(item.getName());
            Intent intent = new Intent(SearchActivity.this, ItemDetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(resultAdapter);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                if (query.isEmpty()) {
                    layoutRecentSearches.setVisibility(View.VISIBLE);
                    gridCategories.setVisibility(View.VISIBLE);
                    findViewById(R.id.tvPopularCategoriesTitle).setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    layoutRecentSearches.setVisibility(View.GONE);
                    gridCategories.setVisibility(View.GONE);
                    findViewById(R.id.tvPopularCategoriesTitle).setVisibility(View.GONE);
                    performSearch(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        List<Item> results = DataManager.getInstance().searchItems(query);
        resultAdapter.updateList(results);

        if (results.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
        }
    }
}
