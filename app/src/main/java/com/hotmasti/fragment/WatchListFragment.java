package com.hotmasti.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotmasti.adapter.WatchListAdapter;
import com.hotmasti.item.ItemWatchList;
import com.hotmasti.util.API;
import com.hotmasti.util.Constant;
import com.hotmasti.util.Events;
import com.hotmasti.util.GlobalBus;
import com.hotmasti.util.NetworkUtils;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.MovieDetailsActivity;
import com.hotmasti.video.MyApplication;
import com.hotmasti.video.R;
import com.hotmasti.video.ShowDetailsActivity;
import com.hotmasti.video.SportDetailsActivity;
import com.hotmasti.video.TVDetailsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WatchListFragment extends Fragment {

    private ArrayList<ItemWatchList> mListItem;
    private RecyclerView recyclerView;
    private WatchListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        GlobalBus.getBus().register(this);
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        if (NetworkUtils.isConnected(getActivity())) {
            getWatchList();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void getWatchList() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", MyApplication.getInstance().getUserId());
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.MY_WATCHLIST_WATCHLIST_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showProgress(false);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemWatchList objItem = new ItemWatchList();
                                objItem.setWatchListId(objJson.getString(Constant.WATCHLIST_ID));
                                objItem.setPostId(objJson.getString(Constant.WATCHLIST_POST_ID));
                                objItem.setPostTitle(objJson.getString(Constant.WATCHLIST_POST_TITLE));
                                objItem.setPostType(objJson.getString(Constant.WATCHLIST_POST_TYPE));
                                objItem.setPostImage(objJson.getString(Constant.WATCHLIST_POST_IMAGE));
                                mListItem.add(objItem);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                lyt_not_found.setVisibility(View.VISIBLE);
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {

            lyt_not_found.setVisibility(View.GONE);
            adapter = new WatchListAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    ItemWatchList itemWatchList = mListItem.get(position);
                    Class<?> aClass;
                    String recentId = itemWatchList.getPostId();
                    String recentType = itemWatchList.getPostType();
                    switch (recentType) {
                        case "Movies":
                            aClass = MovieDetailsActivity.class;
                            break;
                        case "Shows":
                            aClass = ShowDetailsActivity.class;
                            break;
                        case "LiveTV":
                            aClass = TVDetailsActivity.class;
                            break;
                        default:
                            aClass = SportDetailsActivity.class;
                            break;
                    }
                    Intent intent = new Intent(getActivity(), aClass);
                    intent.putExtra("Id", recentId);
                    startActivity(intent);
                }
            });
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getWatchList(Events.WatchList watchList) {
        for (int i = 0; i < mListItem.size(); i++) {
            if (mListItem.get(i).getPostId().equals(watchList.getWatchListId())) {
                if (adapter != null) {
                    mListItem.remove(i);
                    adapter.notifyItemRemoved(i);
                    adapter.notifyItemRangeChanged(i, mListItem.size());
                }
            }
        }
    }
}

