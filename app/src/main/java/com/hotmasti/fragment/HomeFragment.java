package com.hotmasti.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.hotmasti.adapter.HomeMovieAdapter;
import com.hotmasti.adapter.HomeRecentAdapter;
import com.hotmasti.adapter.HomeShowAdapter;
import com.hotmasti.adapter.SliderAdapter;
import com.hotmasti.item.ItemMovie;
import com.hotmasti.item.ItemRecent;
import com.hotmasti.item.ItemShow;
import com.hotmasti.item.ItemSlider;
import com.hotmasti.util.API;
import com.hotmasti.util.Constant;
import com.hotmasti.util.NetworkUtils;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.MainActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {

    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private NestedScrollView nestedScrollView;
    private ViewPager viewPager;
    private TextView latestMovieViewAll, latestShowViewAll, popularMovieViewAll, popularShowViewAll, home3ViewAll, home4ViewAll, home5ViewAll, recentlyViewAll, upComingMovieViewAll, upComingShowViewAll;
    private LinearLayout lytLatestMovie, lytLatestShow, lytPopularMovie, lytPopularShow, lytHome3, lytHome4, lytHome5, lytRecently, lytUpComingMovie, lytUpComingShow;
    private RecyclerView rvLatestMovie, rvLatestShow, rvPopularMovie, rvPopularShow, rvHome3, rvHome4, rvHome5, rvRecently, rvUpComingMovie, rvUpComingShow;
    private CircleIndicator circleIndicator;
    private ArrayList<ItemMovie> latestMovieList, popularMovieList, upComingMovieList;
    private ArrayList<ItemShow> latestShowList, popularShowList, upComingShowList;
    private HomeMovieAdapter latestMovieAdapter, popularMovieAdapter, home3MovieAdapter, home4MovieAdapter, home5MovieAdapter, upComingMovieAdapter;
    private HomeShowAdapter latestShowAdapter, popularShowAdapter, home3ShowAdapter, home4ShowAdapter, home5ShowAdapter, upComingShowAdapter;
    private SliderAdapter sliderAdapter;
    private HomeRecentAdapter recentAdapter;
    private String home3Title, home4Title, home5Title, home3Id, home4Id, home5Id;
    private TextView viewHome3Title, viewHome4Title, viewHome5Title;
    private boolean isHome3Movie = false, isHome4Movie = false, isHome5Movie = false;
    private ArrayList<ItemMovie> home3Movie, home4Movie, home5Movie;
    private ArrayList<ItemShow> home3Show, home4Show, home5Show;
    private ArrayList<ItemSlider> sliderList;
    private ArrayList<ItemRecent> recentList;
    private RelativeLayout lytSlider;
    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        myApplication = MyApplication.getInstance();

        latestMovieList = new ArrayList<>();
        latestShowList = new ArrayList<>();
        popularMovieList = new ArrayList<>();
        popularShowList = new ArrayList<>();
        home3Movie = new ArrayList<>();
        home4Movie = new ArrayList<>();
        home5Movie = new ArrayList<>();
        home3Show = new ArrayList<>();
        home4Show = new ArrayList<>();
        home5Show = new ArrayList<>();
        sliderList = new ArrayList<>();
        recentList = new ArrayList<>();
        upComingMovieList = new ArrayList<>();
        upComingShowList = new ArrayList<>();

        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        viewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);

        latestMovieViewAll = rootView.findViewById(R.id.textHomeLatestMovieViewAll);
        latestShowViewAll = rootView.findViewById(R.id.textHomeLatestShowViewAll);
        popularMovieViewAll = rootView.findViewById(R.id.textHomePopularMovieViewAll);
        popularShowViewAll = rootView.findViewById(R.id.textHomePopularShowViewAll);
        home3ViewAll = rootView.findViewById(R.id.textHome3ViewAll);
        home4ViewAll = rootView.findViewById(R.id.textHome4ViewAll);
        home5ViewAll = rootView.findViewById(R.id.textHome5ViewAll);
        viewHome3Title = rootView.findViewById(R.id.textHome3Name);
        viewHome4Title = rootView.findViewById(R.id.textHome4Name);
        viewHome5Title = rootView.findViewById(R.id.textHome5Name);
        recentlyViewAll = rootView.findViewById(R.id.textHomeRecentlyWatchedViewAll);
        upComingMovieViewAll = rootView.findViewById(R.id.textHomeUpComingMovieViewAll);
        upComingShowViewAll = rootView.findViewById(R.id.textHomeUpComingShowViewAll);

        lytLatestMovie = rootView.findViewById(R.id.lytLatestMovie);
        lytLatestShow = rootView.findViewById(R.id.lytLatestShow);
        lytPopularMovie = rootView.findViewById(R.id.lytPopularMovie);
        lytPopularShow = rootView.findViewById(R.id.lytPopularShow);
        lytHome3 = rootView.findViewById(R.id.lytHome3);
        lytHome4 = rootView.findViewById(R.id.lytHome4);
        lytHome5 = rootView.findViewById(R.id.lytHome5);
        lytSlider = rootView.findViewById(R.id.lytSlider);
        lytRecently = rootView.findViewById(R.id.lytRecentlyWatched);
        lytUpComingShow = rootView.findViewById(R.id.lytUpcomingShow);
        lytUpComingMovie = rootView.findViewById(R.id.lytUpcomingMovie);

        rvLatestMovie = rootView.findViewById(R.id.rv_latest_movie);
        rvLatestShow = rootView.findViewById(R.id.rv_latest_show);
        rvPopularMovie = rootView.findViewById(R.id.rv_popular_movie);
        rvPopularShow = rootView.findViewById(R.id.rv_popular_show);
        rvHome3 = rootView.findViewById(R.id.rv_home3);
        rvHome4 = rootView.findViewById(R.id.rv_home4);
        rvHome5 = rootView.findViewById(R.id.rv_home5);
        rvRecently = rootView.findViewById(R.id.rv_recently_watched);
        rvUpComingMovie = rootView.findViewById(R.id.rv_upcoming_movie);
        rvUpComingShow = rootView.findViewById(R.id.rv_upcoming_show);

        recyclerViewProperty(rvLatestMovie);
        recyclerViewProperty(rvLatestShow);
        recyclerViewProperty(rvPopularMovie);
        recyclerViewProperty(rvPopularShow);
        recyclerViewProperty(rvHome3);
        recyclerViewProperty(rvHome4);
        recyclerViewProperty(rvHome5);
        recyclerViewProperty(rvRecently);
        recyclerViewProperty(rvUpComingMovie);
        recyclerViewProperty(rvUpComingShow);

        if (NetworkUtils.isConnected(getActivity())) {
            getHome();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }


    private void getHome() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.HOME_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);


                    JSONArray sliderArray = liveTVJson.getJSONArray("slider");
                    for (int i = 0; i < sliderArray.length(); i++) {
                        JSONObject jsonObject = sliderArray.getJSONObject(i);
                        ItemSlider itemSlider = new ItemSlider();
                        itemSlider.setId(jsonObject.getString("slider_post_id"));
                        itemSlider.setSliderTitle(jsonObject.getString("slider_title"));
                        itemSlider.setSliderImage(jsonObject.getString("slider_image"));
                        itemSlider.setSliderType(jsonObject.getString("slider_type"));
                        sliderList.add(itemSlider);
                    }

                    JSONArray recentArray = liveTVJson.getJSONArray("recently_watched");
                    for (int i = 0; i < recentArray.length(); i++) {
                        JSONObject jsonObject = recentArray.getJSONObject(i);
                        ItemRecent itemRecent = new ItemRecent();
                        itemRecent.setRecentId(jsonObject.getString("video_id"));
                        itemRecent.setRecentImage(jsonObject.getString("video_thumb_image"));
                        itemRecent.setRecentType(jsonObject.getString("video_type"));
                        recentList.add(itemRecent);
                    }
                    if (liveTVJson.has("latest_movies")) {
                        JSONArray latestMovieArray = liveTVJson.getJSONArray("latest_movies");
                        for (int i = 0; i < latestMovieArray.length(); i++) {
                            JSONObject objJson = latestMovieArray.getJSONObject(i);
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            latestMovieList.add(objItem);
                        }
                    }
                    if (liveTVJson.has("upcoming_movies")) {
                        JSONArray latestMovieArray = liveTVJson.getJSONArray("upcoming_movies");
                        for (int i = 0; i < latestMovieArray.length(); i++) {
                            JSONObject objJson = latestMovieArray.getJSONObject(i);
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            upComingMovieList.add(objItem);
                        }
                    }
                    if (liveTVJson.has("upcoming_series")) {
                        JSONArray latestShowArray = liveTVJson.getJSONArray("upcoming_series");
                        for (int i = 0; i < latestShowArray.length(); i++) {
                            JSONObject objJson = latestShowArray.getJSONObject(i);
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            upComingShowList.add(objItem);
                        }
                    }
                    if (liveTVJson.has("latest_shows")) {
                        JSONArray latestShowArray = liveTVJson.getJSONArray("latest_shows");
                        for (int i = 0; i < latestShowArray.length(); i++) {
                            JSONObject objJson = latestShowArray.getJSONObject(i);
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            latestShowList.add(objItem);
                        }
                    }
                    if (liveTVJson.has("popular_movies")) {
                        JSONArray popularMovieArray = liveTVJson.getJSONArray("popular_movies");
                        for (int i = 0; i < popularMovieArray.length(); i++) {
                            JSONObject objJson = popularMovieArray.getJSONObject(i);
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            popularMovieList.add(objItem);
                        }
                    }

                    if (liveTVJson.has("popular_shows")) {
                        JSONArray popularShowArray = liveTVJson.getJSONArray("popular_shows");
                        for (int i = 0; i < popularShowArray.length(); i++) {
                            JSONObject objJson = popularShowArray.getJSONObject(i);
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            popularShowList.add(objItem);
                        }
                    }

                    home3Title = liveTVJson.getString("home_sections3_title");
                    home3Id = liveTVJson.getString("home_sections3_lang_id");
                    isHome3Movie = liveTVJson.getString("home_sections3_type").equals("Movie");

                    JSONArray home3Array = liveTVJson.getJSONArray("home_sections3");
                    for (int i = 0; i < home3Array.length(); i++) {
                        JSONObject objJson = home3Array.getJSONObject(i);
                        if (isHome3Movie) {
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            home3Movie.add(objItem);
                        } else {
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            home3Show.add(objItem);
                        }
                    }

                    home4Title = liveTVJson.getString("home_sections4_title");
                    home4Id = liveTVJson.getString("home_sections4_lang_id");
                    isHome4Movie = liveTVJson.getString("home_sections4_type").equals("Movie");

                    JSONArray home4Array = liveTVJson.getJSONArray("home_sections4");
                    for (int i = 0; i < home4Array.length(); i++) {
                        JSONObject objJson = home4Array.getJSONObject(i);
                        if (isHome4Movie) {
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            home4Movie.add(objItem);
                        } else {
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            home4Show.add(objItem);
                        }
                    }


                    home5Title = liveTVJson.getString("home_sections5_title");
                    home5Id = liveTVJson.getString("home_sections5_lang_id");
                    isHome5Movie = liveTVJson.getString("home_sections5_type").equals("Movie");

                    JSONArray home5Array = liveTVJson.getJSONArray("home_sections5");
                    for (int i = 0; i < home5Array.length(); i++) {
                        JSONObject objJson = home5Array.getJSONObject(i);
                        if (isHome5Movie) {
                            ItemMovie objItem = new ItemMovie();
                            objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                            objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            home5Movie.add(objItem);
                        } else {
                            ItemShow objItem = new ItemShow();
                            objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                            objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                            objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            home5Show.add(objItem);
                        }
                    }


                    displayData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nestedScrollView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {

        if (!sliderList.isEmpty()) {
            sliderAdapter = new SliderAdapter(requireActivity(), sliderList);
            viewPager.setAdapter(sliderAdapter);
            circleIndicator.setViewPager(viewPager);
        } else {
            lytSlider.setVisibility(View.GONE);
        }

        if (!recentList.isEmpty()) {
            recentAdapter = new HomeRecentAdapter(getActivity(), recentList);
            rvRecently.setAdapter(recentAdapter);

            recentAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    ItemRecent itemRecent = recentList.get(position);
                    Class<?> aClass;
                    String recentId = itemRecent.getRecentId();
                    String recentType = itemRecent.getRecentType();
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

        } else {
            lytRecently.setVisibility(View.GONE);
        }

        if (!latestMovieList.isEmpty()) {
            latestMovieAdapter = new HomeMovieAdapter(getActivity(), latestMovieList, false);
            rvLatestMovie.setAdapter(latestMovieAdapter);

            latestMovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = latestMovieList.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytLatestMovie.setVisibility(View.GONE);
        }

        if (!upComingMovieList.isEmpty()) {
            upComingMovieAdapter = new HomeMovieAdapter(getActivity(), upComingMovieList, false);
            rvUpComingMovie.setAdapter(upComingMovieAdapter);

            upComingMovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = upComingMovieList.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytUpComingMovie.setVisibility(View.GONE);
        }

        if (!upComingShowList.isEmpty()) {
            upComingShowAdapter = new HomeShowAdapter(getActivity(), upComingShowList, false);
            rvUpComingShow.setAdapter(upComingShowAdapter);

            upComingShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = upComingShowList.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                }
            });
        } else {
            lytUpComingShow.setVisibility(View.GONE);
        }

        if (!latestShowList.isEmpty()) {
            latestShowAdapter = new HomeShowAdapter(getActivity(), latestShowList, false);
            rvLatestShow.setAdapter(latestShowAdapter);

            latestShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = latestShowList.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                }
            });
        } else {
            lytLatestShow.setVisibility(View.GONE);
        }

        if (!popularMovieList.isEmpty()) {
            popularMovieAdapter = new HomeMovieAdapter(getActivity(), popularMovieList, false);
            rvPopularMovie.setAdapter(popularMovieAdapter);

            popularMovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = popularMovieList.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytPopularMovie.setVisibility(View.GONE);
        }

        if (!popularShowList.isEmpty()) {
            popularShowAdapter = new HomeShowAdapter(getActivity(), popularShowList, false);
            rvPopularShow.setAdapter(popularShowAdapter);

            popularShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = popularShowList.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                }
            });

        } else {
            lytPopularShow.setVisibility(View.GONE);
        }

        viewHome3Title.setText(home3Title);
        if (isHome3Movie) {
            if (!home3Movie.isEmpty()) {
                home3MovieAdapter = new HomeMovieAdapter(getActivity(), home3Movie, false);
                rvHome3.setAdapter(home3MovieAdapter);

                home3MovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String movieId = home3Movie.get(position).getMovieId();
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                        intent.putExtra("Id", movieId);
                        startActivity(intent);
                    }
                });

            } else {
                lytHome3.setVisibility(View.GONE);
            }
        } else {
            if (!home3Show.isEmpty()) {
                home3ShowAdapter = new HomeShowAdapter(getActivity(), home3Show, false);
                rvHome3.setAdapter(home3ShowAdapter);

                home3ShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String showId = home3Show.get(position).getShowId();
                        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                        intent.putExtra("Id", showId);
                        startActivity(intent);
                    }
                });

            } else {
                lytHome3.setVisibility(View.GONE);
            }
        }


        viewHome4Title.setText(home4Title);
        if (isHome4Movie) {
            if (!home4Movie.isEmpty()) {
                home4MovieAdapter = new HomeMovieAdapter(getActivity(), home4Movie, false);
                rvHome4.setAdapter(home4MovieAdapter);

                home4MovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String movieId = home4Movie.get(position).getMovieId();
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                        intent.putExtra("Id", movieId);
                        startActivity(intent);
                    }
                });
            } else {
                lytHome4.setVisibility(View.GONE);
            }
        } else {
            if (!home4Show.isEmpty()) {
                home4ShowAdapter = new HomeShowAdapter(getActivity(), home4Show, false);
                rvHome4.setAdapter(home4ShowAdapter);

                home4ShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String showId = home4Show.get(position).getShowId();
                        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                        intent.putExtra("Id", showId);
                        startActivity(intent);
                    }
                });
            } else {
                lytHome4.setVisibility(View.GONE);
            }
        }


        viewHome5Title.setText(home5Title);
        if (isHome5Movie) {
            if (!home5Movie.isEmpty()) {
                home5MovieAdapter = new HomeMovieAdapter(getActivity(), home5Movie, false);
                rvHome5.setAdapter(home5MovieAdapter);

                home5MovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String movieId = home5Movie.get(position).getMovieId();
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                        intent.putExtra("Id", movieId);
                        startActivity(intent);
                    }
                });

            } else {
                lytHome5.setVisibility(View.GONE);
            }
        } else {
            if (!home5Show.isEmpty()) {
                home5ShowAdapter = new HomeShowAdapter(getActivity(), home5Show, false);
                rvHome5.setAdapter(home5ShowAdapter);

                home5ShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String showId = home5Show.get(position).getShowId();
                        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                        intent.putExtra("Id", showId);
                        startActivity(intent);
                    }
                });
            } else {
                lytHome5.setVisibility(View.GONE);
            }
        }

        latestMovieViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getString(R.string.home_latest_movie);
                HomeMovieMoreFragment homeMovieMoreFragment = new HomeMovieMoreFragment();
                Bundle bundleShow = new Bundle();
                bundleShow.putString("Id", "");
                bundleShow.putString("movieUrl", "latest_movies");
                homeMovieMoreFragment.setArguments(bundleShow);
                changeFragment(homeMovieMoreFragment, title);
            }
        });

        popularMovieViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getString(R.string.home_popular_movie);
                HomeMovieMoreFragment homeMovieMoreFragment = new HomeMovieMoreFragment();
                Bundle bundleShow = new Bundle();
                bundleShow.putString("Id", "");
                bundleShow.putString("movieUrl", "popular_movies");
                homeMovieMoreFragment.setArguments(bundleShow);
                changeFragment(homeMovieMoreFragment, title);
            }
        });

        latestShowViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getString(R.string.home_latest_show);
                HomeShowMoreFragment homeShowMoreFragment = new HomeShowMoreFragment();
                Bundle bundleShow = new Bundle();
                bundleShow.putString("Id", "");
                bundleShow.putString("showUrl", "latest_shows");
                homeShowMoreFragment.setArguments(bundleShow);
                changeFragment(homeShowMoreFragment, title);
            }
        });


        popularShowViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getString(R.string.home_popular_show);
                HomeShowMoreFragment homeShowMoreFragment = new HomeShowMoreFragment();
                Bundle bundleShow = new Bundle();
                bundleShow.putString("Id", "");
                bundleShow.putString("showUrl", "popular_shows");
                homeShowMoreFragment.setArguments(bundleShow);
                changeFragment(homeShowMoreFragment, title);
            }
        });

        home3ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = home3Title;
                Bundle bundle = new Bundle();
                bundle.putString("Id", home3Id);
                if (isHome3Movie) {
                    HomeMovieMoreFragment homeMovieMoreFragment = new HomeMovieMoreFragment();
                    homeMovieMoreFragment.setArguments(bundle);
                    changeFragment(homeMovieMoreFragment, title);
                } else {
                    HomeShowMoreFragment homeShowMoreFragment = new HomeShowMoreFragment();
                    homeShowMoreFragment.setArguments(bundle);
                    changeFragment(homeShowMoreFragment, title);
                }
            }
        });


        home4ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = home4Title;
                Bundle bundle = new Bundle();
                bundle.putString("Id", home4Id);
                if (isHome4Movie) {
                    HomeMovieMoreFragment homeMovieMoreFragment = new HomeMovieMoreFragment();
                    homeMovieMoreFragment.setArguments(bundle);
                    changeFragment(homeMovieMoreFragment, title);
                } else {
                    HomeShowMoreFragment homeShowMoreFragment = new HomeShowMoreFragment();
                    homeShowMoreFragment.setArguments(bundle);
                    changeFragment(homeShowMoreFragment, title);
                }
            }
        });


        home5ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = home5Title;
                Bundle bundle = new Bundle();
                bundle.putString("Id", home5Id);
                if (isHome5Movie) {
                    HomeMovieMoreFragment homeMovieMoreFragment = new HomeMovieMoreFragment();
                    homeMovieMoreFragment.setArguments(bundle);
                    changeFragment(homeMovieMoreFragment, title);
                } else {
                    HomeShowMoreFragment homeShowMoreFragment = new HomeShowMoreFragment();
                    homeShowMoreFragment.setArguments(bundle);
                    changeFragment(homeShowMoreFragment, title);
                }
            }
        });


    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(HomeFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }
}
