package com.hotmasti.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotmasti.adapter.HomeMovieAdapter;
import com.hotmasti.item.ItemMovie;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.MovieDetailsActivity;
import com.hotmasti.video.R;

import java.util.ArrayList;

public class ActorMovieFragment extends Fragment {

    public static ArrayList<ItemMovie> mListItem;
    private RecyclerView recyclerView;
    private HomeMovieAdapter adapter;
    private LinearLayout lyt_not_found;

    public static ActorMovieFragment newInstance(ArrayList<ItemMovie> mList) {
        ActorMovieFragment f = new ActorMovieFragment();
        mListItem = mList;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        displayData();
        return rootView;
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
            adapter = new HomeMovieAdapter(getActivity(), mListItem, true);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = mListItem.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });
        }
    }
}

