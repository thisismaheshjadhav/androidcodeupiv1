package com.hotmasti.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hotmasti.fragment.ActorMovieFragment;
import com.hotmasti.fragment.ActorShowFragment;
import com.hotmasti.item.ItemMovie;
import com.hotmasti.item.ItemShow;
import com.hotmasti.util.API;
import com.hotmasti.util.Constant;
import com.hotmasti.util.IsRTL;
import com.hotmasti.util.NetworkUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ActorDirectorDetailActivity extends AppCompatActivity {

    String adId, adName;
    boolean isActor = false;
    ProgressBar mProgressBar;
    LinearLayout lytContent, lyt_not_found;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<ItemMovie> mListMovies;
    ArrayList<ItemShow> mListShow;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_director_details);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        adName = intent.getStringExtra("adName");
        isActor = intent.getBooleanExtra("isActor", false);
        mListMovies = new ArrayList<>();
        mListShow = new ArrayList<>();

        setTitle(adName);
        mProgressBar = findViewById(R.id.progressBar1);
        lytContent = findViewById(R.id.content);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        if (NetworkUtils.isConnected(ActorDirectorDetailActivity.this)) {
            getActorDirectorDetail();
        } else {
            Toast.makeText(ActorDirectorDetailActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }

    private void getActorDirectorDetail() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty(isActor ? "a_id" : "d_id", adId);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(isActor ? Constant.ACTOR_DETAILS_URL : Constant.DIRECTOR_DETAILS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytContent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytContent.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    if (liveTVJson.length() > 0) {
                        Log.e("yes", "-->");
                        if (liveTVJson.has("movies")) {
                            JSONArray latestMovieArray = liveTVJson.getJSONArray("movies");
                            for (int i = 0; i < latestMovieArray.length(); i++) {
                                JSONObject objJson = latestMovieArray.getJSONObject(i);
                                ItemMovie objItem = new ItemMovie();
                                objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                                mListMovies.add(objItem);
                            }
                        }

                        if (liveTVJson.has("shows")) {
                            JSONArray latestShowArray = liveTVJson.getJSONArray("shows");
                            for (int i = 0; i < latestShowArray.length(); i++) {
                                JSONObject objJson = latestShowArray.getJSONObject(i);
                                ItemShow objItem = new ItemShow();
                                objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                mListShow.add(objItem);
                            }
                        }

                        displayData();
                    } else {
                        Log.e("NO", "-->");
                        mProgressBar.setVisibility(View.GONE);
                        lytContent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                mProgressBar.setVisibility(View.GONE);
                lytContent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(20, 0, 20, 0);
            tab.requestLayout();
        }
        Log.e("mList", "==" + mListMovies.size());
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ActorMovieFragment.newInstance(mListMovies), getString(R.string.menu_movie));
        adapter.addFragment(ActorShowFragment.newInstance(mListShow), getString(R.string.menu_tv_show));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return FragmentStatePagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
