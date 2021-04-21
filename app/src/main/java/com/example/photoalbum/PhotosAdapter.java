package com.example.photoalbum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    boolean isSelectAll = false;
    ArrayList<PhotoData> selectedList = new ArrayList<>();
    private Context context;
    private ArrayList<PhotoData> photos;
    private ItemClick itemClick;
    private ActionMode actionMode;

    public PhotosAdapter(Context context, PhotosFragment activity, ArrayList<PhotoData> data) {
        this.context = context;
        this.photos = data;
        this.itemClick = (ItemClick) activity;
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

        holder.thumbnail_cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionMode != null) {
                    // if not null then already activated so select the item
                    longClickItem(holder, actionMode);
                    return true;
                }
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // Inflating menu upon creating action mode
                        mode.getMenuInflater().inflate(R.menu.select_action_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        // when preparing action mode
                        longClickItem(holder, mode);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        // when click on action mode item check for which menu item clicked
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.menu_delete:
                                // deleting selected photos
                                if (selectedList.size() == 0) {
                                    Toast.makeText(context, "0 Images selected", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                                // show dialog before deleting
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("Are you sure you want to delete this?");
                                dialog.setCancelable(true);
                                dialog.setTitle("Delete photos");
                                dialog.setPositiveButton("yes", (dialogInterface, i) -> {
                                    deleteSelectedItems(selectedList, mode);
                                });
                                dialog.setNegativeButton("Cancel", null);
                                dialog.show();
                                break;
                            case R.id.menu_select_all:
                                if (selectedList.size() == photos.size()) {
                                    isSelectAll = false;
                                    selectedList.clear();
                                } else {
                                    isSelectAll = true;
                                    // clear selected array
                                    selectedList.clear();
                                    selectedList.addAll(photos);
                                }
                                notifyDataSetChanged();
                                break;
                            case R.id.menu_share:
                                if (selectedList.size() == 0) {
                                    Toast.makeText(context, "0 Images selected", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                Toast.makeText(context, "Share Images", Toast.LENGTH_SHORT).show();

                                // Generating URI from file provider
                                // guide: https://medium.com/@ali.muzaffar/what-is-android-os-fileuriexposedexception-and-what-you-can-do-about-it-70b9eb17c6d0#.54odzsnk4
                                ArrayList<Uri> files = new ArrayList<>();
                                for (PhotoData image : selectedList) {
                                    files.add(FileProvider.getUriForFile(context,
                                            context.getApplicationContext().getPackageName() + ".provider",
                                            new File(image.getPhotoLocation()))
                                    );
                                }

                                // creating share intent
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                shareIntent.setType("image/*");
                                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(shareIntent);

                                break;
                            default:
                                return false;
                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // when action mode is destroyed / closed
                        isSelectAll = false;
                        selectedList.clear();
                        notifyDataSetChanged();
                        actionMode = null;
                    }
                };
                actionMode = ((AppCompatActivity) view.getContext()).startSupportActionMode(callback);
                return true;
            }
        });

        holder.thumbnail_cardView.setOnClickListener(v -> {
            if (actionMode != null) {
                // when action mode is enabled
                // select image
                longClickItem(holder, this.actionMode);
            } else {
                // display picture
                itemClick.onItemClicked(photos.indexOf((PhotoData) holder.itemView.getTag()));
            }
        });

        if (isSelectAll) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.thumbnail.setBackground(ContextCompat.getDrawable(context, R.drawable.image_view_border));
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.thumbnail.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void longClickItem(ViewHolder holder, ActionMode mode) {
        // Get selected item value
        PhotoData data = photos.get(holder.getAdapterPosition());
        if (holder.checkbox.getVisibility() == View.GONE) {
            // when item not selected
            // Visible check box
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.thumbnail.setBackground(ContextCompat.getDrawable(context, R.drawable.image_view_border));
            selectedList.add(data);
        } else {
            // when item selected
            //hide check box image
            holder.checkbox.setVisibility(View.GONE);
            holder.thumbnail.setBackgroundColor(Color.TRANSPARENT);
            selectedList.remove(data);
        }
        //change title
        mode.setTitle(selectedList.size() + " Selected");
    }

    private void deleteSelectedItems(ArrayList<PhotoData> selectedItems, ActionMode mode) {
        int delete_count = 0;
        for (PhotoData item : selectedItems) {
            // deleting from media store
            int deleted_rows = context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA + " = ?", new String[]{item.getPhotoLocation()});
            if (deleted_rows == 1) {
                // deleting from storage
                new File(item.getPhotoLocation()).delete();
                // deleting from recycler view array
                photos.remove(item);
                delete_count++;
            }
        }
        Toast.makeText(context, delete_count + " Selected Images deleted.", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged(); // notify recycler view
        mode.finish(); // close action mode
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public interface ItemClick {
        void onItemClicked(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        CardView thumbnail_cardView;
        ImageView checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_photo_thumbnail);
            thumbnail_cardView = itemView.findViewById(R.id.cv_photos_card_view);
            checkbox = itemView.findViewById(R.id.iv_select_checkbox);
        }
    }
}
