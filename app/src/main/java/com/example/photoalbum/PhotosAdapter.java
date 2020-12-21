package com.example.photoalbum;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PhotoData> photos;
    private ItemClick itemClick;

    public PhotosAdapter(Context context, PhotosFragment activity, ArrayList<PhotoData> data) {
        this.context = context;
        this.photos = data;
        this.itemClick = (ItemClick) activity;
    }

    public interface ItemClick {
        void onItemClicked(int pos);
    }

    @NonNull
    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(photos.get(position));
        Glide.with(context) //Using glide dependency to generate thumbnails
                .load(Uri.fromFile(new File(photos.get(position).getPhotoLocation())))
                .centerCrop()
                .into(holder.thumbnail); // show the image to passed variable
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        CardView thumbnail_cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_photo_thumbnail);
            thumbnail_cardView = itemView.findViewById(R.id.cv_photos_card_view);
            thumbnail_cardView.setOnClickListener(v -> {
                itemClick.onItemClicked(photos.indexOf((PhotoData) itemView.getTag()));
            });
        }
    }
}
