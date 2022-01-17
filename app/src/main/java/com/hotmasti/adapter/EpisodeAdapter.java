package com.hotmasti.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotmasti.item.ItemEpisode;
import com.hotmasti.util.NetworkUtils;
import com.hotmasti.util.PopUpAds;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.util.ShareUtils;
import com.hotmasti.video.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemEpisode> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;
    private boolean isPurchased;

    public EpisodeAdapter(Context context, ArrayList<ItemEpisode> dataList, boolean isPurchased) {
        this.dataList = dataList;
        this.mContext = context;
        this.isPurchased = isPurchased;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_episode_item_new, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemEpisode singleItem = dataList.get(position);
        holder.text.setText(singleItem.getEpisodeName());
        holder.textTime.setText(singleItem.getEpisodeDuration());
        holder.textDate.setText(singleItem.getEpisodeDate());
        holder.textDesc.setVisibility(singleItem.getEpisodeDescription().isEmpty() ? View.GONE : View.VISIBLE);
        holder.textDesc.setText(NetworkUtils.html2text(singleItem.getEpisodeDescription()));
        holder.tvView.setText(singleItem.getEpisodeView());
        Picasso.get().load(singleItem.getEpisodeImage()).into(holder.imageEpisode);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        if (row_index > -1) {
            if (row_index == position) {
                holder.imagePlay.setVisibility(View.VISIBLE);
            } else {
                holder.imagePlay.setVisibility(View.GONE);
            }

        }

        if (singleItem.isDownload()) {
            if (singleItem.isPremium()) {
                if (isPurchased) {
                    holder.imageDownload.setVisibility(View.VISIBLE);
                } else {
                    holder.imageDownload.setVisibility(View.GONE);
                }
            } else {
                holder.imageDownload.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageDownload.setVisibility(View.GONE);
        }

        holder.imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singleItem.getDownloadUrl().isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.download_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mContext.startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(singleItem.getDownloadUrl())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(mContext, mContext.getString(R.string.invalid_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareFacebook((Activity) mContext, singleItem.getEpisodeName(), singleItem.getEpisodeShareLink());
            }
        });

        holder.imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTwitter((Activity) mContext, singleItem.getEpisodeName(), singleItem.getEpisodeShareLink(), "", "");
            }
        });

        holder.imgWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareWhatsapp((Activity) mContext, singleItem.getEpisodeName(), singleItem.getEpisodeShareLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text, textTime, textDesc, textDate, tvView;
        ImageView imagePlay, imageDownload, imgFacebook, imgTwitter, imgWhatsApp;
        RelativeLayout rootLayout;
        RoundedImageView imageEpisode;

        ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textEpisodes);
            imagePlay = itemView.findViewById(R.id.imageEpPlay);
            textTime = itemView.findViewById(R.id.textDuration);
            textDesc = itemView.findViewById(R.id.textDesc);
            textDate = itemView.findViewById(R.id.textDate);
            tvView = itemView.findViewById(R.id.tvView);
            imageEpisode = itemView.findViewById(R.id.imageEp);
            imagePlay = itemView.findViewById(R.id.imageEpPlay);
            imgFacebook = itemView.findViewById(R.id.imgFacebook);
            imgTwitter = itemView.findViewById(R.id.imgTwitter);
            imgWhatsApp = itemView.findViewById(R.id.imgWhatsApp);
            imageDownload = itemView.findViewById(R.id.imageEpDownload);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
