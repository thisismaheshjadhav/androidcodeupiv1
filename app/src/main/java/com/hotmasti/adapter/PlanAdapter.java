package com.hotmasti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotmasti.item.ItemPlan;
import com.hotmasti.util.RvOnClickListener;
import com.hotmasti.video.R;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemPlan> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;

    public PlanAdapter(Context context, ArrayList<ItemPlan> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_plan, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemPlan singleItem = dataList.get(position);
        holder.textPlanName.setText(singleItem.getPlanName());
        holder.textPlanPrice.setText(singleItem.getPlanPrice());
        holder.textPlanCurrency.setText(singleItem.getPlanCurrencyCode());
        holder.textPlanDuration.setText(mContext.getString(R.string.plan_day_for, singleItem.getPlanDuration()));
        holder.lytPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position);
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(position);
            }
        });


        if (row_index > -1) {
            if (row_index == position) {
                holder.lytPlan.setBackgroundResource(R.drawable.plan_select);
                holder.textPlanName.setTextColor(mContext.getResources().getColor(R.color.sub_pack_select));
                holder.textPlanDuration.setTextColor(mContext.getResources().getColor(R.color.highlight));
                holder.textPlanPrice.setTextColor(mContext.getResources().getColor(R.color.sub_price_select));
                holder.textPlanCurrency.setTextColor(mContext.getResources().getColor(R.color.sub_price_select));
                holder.radioButton.setChecked(true);
            } else {
                holder.lytPlan.setBackgroundResource(R.drawable.plan_normal);
                holder.textPlanName.setTextColor(mContext.getResources().getColor(R.color.sub_pack_normal));
                holder.textPlanDuration.setTextColor(mContext.getResources().getColor(R.color.sub_pack_normal));
                holder.textPlanPrice.setTextColor(mContext.getResources().getColor(R.color.sub_price_normal));
                holder.textPlanCurrency.setTextColor(mContext.getResources().getColor(R.color.sub_price_normal));
                holder.radioButton.setChecked(false);
            }

        }

    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView textPlanName;
        TextView textPlanPrice;
        TextView textPlanDuration;
        TextView textPlanCurrency;
        RadioButton radioButton;
        RelativeLayout lytPlan;

        ItemRowHolder(View itemView) {
            super(itemView);
            textPlanName = itemView.findViewById(R.id.textPackName);
            textPlanPrice = itemView.findViewById(R.id.textPrice);
            textPlanDuration = itemView.findViewById(R.id.textDay);
            textPlanCurrency = itemView.findViewById(R.id.textCurrency);
            radioButton = itemView.findViewById(R.id.radioButton);
            lytPlan = itemView.findViewById(R.id.lytPlan);
        }
    }

}
