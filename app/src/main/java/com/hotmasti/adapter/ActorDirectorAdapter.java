package com.hotmasti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotmasti.item.ItemActor;
import com.hotmasti.util.PopUpAds;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActorDirectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemActor> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;

    public ActorDirectorAdapter(Context context, ArrayList<ItemActor> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_actor_director_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemActor singleItem = dataList.get(position);
        holder.text.setText(singleItem.getActorName());

        Picasso.get().load(singleItem.getActorImage()).into(holder.image);
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
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

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageActor);
            text = itemView.findViewById(R.id.text);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
