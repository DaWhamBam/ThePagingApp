package com.example.thepagingapp2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.example.thepagingapp2.R;
import com.example.thepagingapp2.adapter.MoviesAdapter;
import com.example.thepagingapp2.adapter.MoviesLoadStateAdapter;
import com.example.thepagingapp2.databinding.ActivityMainBinding;
import com.example.thepagingapp2.util.GridSpace;
import com.example.thepagingapp2.util.MovieComparator;
import com.example.thepagingapp2.util.Utils;
import com.example.thepagingapp2.viewmodel.MovieViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    MovieViewModel mainActivityViewModel;
    ActivityMainBinding binding;
    MoviesAdapter moviesAdapter;

    @Inject
    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (Utils.API_KEY == null || Utils.API_KEY.isEmpty()) {
            Toast.makeText(this, "Error in API Key", Toast.LENGTH_SHORT).show();
        }

        moviesAdapter = new MoviesAdapter(new MovieComparator(), requestManager);

        mainActivityViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        initRecyclerViewAndAdapter();

        mainActivityViewModel.moviePagingDataFlowable.subscribe(moviePagingData -> {
            moviesAdapter.submitData(getLifecycle(), moviePagingData);
        });
    }

    private void initRecyclerViewAndAdapter() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.recyclerView.addItemDecoration(new GridSpace(2, 20, true));

        binding.recyclerView.setAdapter(
                moviesAdapter.withLoadStateFooter(
                        new MoviesLoadStateAdapter(view -> {
                            moviesAdapter.retry();
                        })
                )
        );

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return moviesAdapter.getItemViewType(position) == MoviesAdapter.LOADING_ITEM ? 1 : 2;
            }
        });
    }
}