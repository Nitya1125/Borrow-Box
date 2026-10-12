package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.ItemGridAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvSubtitle;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvItems;
    private LinearLayout layoutEmpty;

    private ItemGridAdapter adapter;
    private final List<Item> savedItemList = new ArrayList<>();
    private ItemRepository itemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        itemRepository = ItemRepository.getInstance(this);

        btnBack = findViewById(R.id.btnWishlistBack);
        tvSubtitle = findViewById(R.id.tvWishlistSubtitle);
        swipeRefresh = findViewById(R.id.swipeRefreshWishlist);
        rvItems = findViewById(R.id.rvWishlistItems);
        layoutEmpty = findViewById(R.id.layoutWishlistEmpty);

        btnBack.setOnClickListener(v -> finish());

        swipeRefresh.setOnRefreshListener(this::loadWishlist);

        adapter = new ItemGridAdapter(this, savedItemList, item -> {
            Intent intent = new Intent(WishlistActivity.this, ItemDetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });

        rvItems.setLayoutManager(new GridLayoutManager(this, 2));
        rvItems.setAdapter(adapter);

        loadWishlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist();
    }

    private void loadWishlist() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        itemRepository.getSavedItems(user.getId(), new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                swipeRefresh.setRefreshing(false);
                savedItemList.clear();
                if (items != null && !items.isEmpty()) {
                    savedItemList.addAll(items);
                    rvItems.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    tvSubtitle.setText(items.size() + (items.size() == 1 ? " saved item" : " saved items"));
                } else {
                    rvItems.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    tvSubtitle.setText("0 saved items");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable t) {
                swipeRefresh.setRefreshing(false);
                rvItems.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                tvSubtitle.setText("0 saved items");
            }
        });
    }
}
