package com.example.rishiprotimbose.preorderapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rishiprotimbose.preorderapp.Common.Common;
import com.example.rishiprotimbose.preorderapp.CustomerProfileActivity;
import com.example.rishiprotimbose.preorderapp.Interface.ItemClickListener;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView img;
    public TextView tvName;
    public TextView tvPhoneNumber;
    public TextView tvEmail;
    public RatingBar rating;

    ItemClickListener itemClickListener;

    public CustomViewHolder(View itemView) {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.icon);
        tvName = (TextView) itemView.findViewById(R.id.firstLine);
        tvPhoneNumber = (TextView) itemView.findViewById(R.id.secondLine);
        tvEmail = (TextView) itemView.findViewById(R.id.thirdLine);
        rating = (RatingBar) itemView.findViewById(R.id.rating);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    List<String> items;
    Context context;

    int row_index = -1;

    public CustomAdapter(Set<String> items, Context context) {
        this.items = new ArrayList<String>(items);
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.listviewlay, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Users new_user = CustomerProfileActivity.ruk_u.get(items.get(position));
        //    holder.img.setImageDrawable();
        holder.tvName.setText(new_user.getName());
        holder.tvPhoneNumber.setText(new_user.getPhoneNumber());
        holder.tvEmail.setText(new_user.getEmail());
        holder.rating.setRating(2);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                row_index = position;
                Common.currentItem = items.get(position);
                CustomerProfileActivity.businesstype[0] = "Restaurants";
                CustomerProfileActivity.businesstype[1] = items.get(position);
                notifyDataSetChanged();
            }
        });
        if(row_index == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#AAA8A8AA"));
//            holder.itemView.setTextColor(Color.parseColor("#C5C5C7"));
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
//            holder.itemView.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
