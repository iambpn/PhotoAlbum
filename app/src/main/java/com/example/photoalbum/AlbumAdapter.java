package com.example.photoalbum;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<AlbumData> albums;
    private Context context;
    private ItemClicked itemClicked;

    public AlbumAdapter(Context context, AlbumFragment activity, ArrayList<AlbumData> albums) {
        this.albums = albums;
        this.context = context;
        this.itemClicked = (ItemClicked) activity;
    }

    public interface ItemClicked {
        void onItemClicked(int pos);
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(albums.get(position));

        holder.albumName.setText(albums.get(position).getFolderName());
        Glide.with(context)
                .load(Uri.fromFile(new File(albums.get(position).getThumbnailImage())))
                .centerCrop()
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView thumbnail;
        LinearLayout album_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.tv_album_name);
            thumbnail = itemView.findViewById(R.id.iv_album_thumbnail);
            album_item = itemView.findViewById(R.id.ll_album_item);

            album_item.setOnClickListener(v -> {
                itemClicked.onItemClicked(albums.indexOf((AlbumData) itemView.getTag()));
            });
        }
    }
}
