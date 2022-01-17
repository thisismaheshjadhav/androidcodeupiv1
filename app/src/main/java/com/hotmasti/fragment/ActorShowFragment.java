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

import com.hotmasti.adapter.HomeShowAdapter;
import com.hotmasti.item.ItemShow;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.R;
import com.hotmasti.video.ShowDetailsActivity;

import java.util.ArrayList;

public class ActorShowFragment extends Fragment {

    private static ArrayList<ItemShow> mListItem;
    private RecyclerView recyclerView;
    private HomeShowAdapter adapter;
    private LinearLayout lyt_not_found;

    public static ActorShowFragment newInstance(ArrayList<ItemShow> mList) {
        ActorShowFragment f = new ActorShowFragment();
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        displayData();
        return rootView;
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {

            lyt_not_found.setVisibility(View.GONE);
            adapter = new HomeShowAdapter(getActivity(), mListItem, true);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = mListItem.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                }
            });
        }
    }
}

