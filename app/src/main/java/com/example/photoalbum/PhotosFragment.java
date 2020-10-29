package com.example.photoalbum;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotosFragment extends Fragment implements PhotosAdapter.ItemClick {
    private static final String FOLDER_PATH = "folderPath";

    private String folderPath;
    private GetSetData getSetData;
    private ArrayList<String> photosPath;
    private ArrayList<PhotoData> photosObject;
    private PhotosAdapter adapter;
    private RecyclerView recyclerView;

    public static PhotosFragment newInstance(String folderPath) { // Factory Method
        Bundle args = new Bundle();
        args.putString(FOLDER_PATH, folderPath); // set key and argument value

        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args); // set argument
        return fragment;
    }

    public interface GetSetData {
        ArrayList<String> getPhotosPathOfFolder(String folderPath);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.getSetData = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        folderPath = getArguments().getString(FOLDER_PATH); // getting value from bundle
        photosPath = getSetData.getPhotosPathOfFolder(folderPath); // call to MainActivity

        photosObject = new ArrayList<>();
        adapter = new PhotosAdapter(getContext(), this, photosObject);
        recyclerView = view.findViewById(R.id.rv_photos_recycler_view);
        String[] pathSplit;
        for (String path : photosPath) {
            pathSplit = path.split("/");
            photosObject.add(new PhotoData(path, pathSplit[pathSplit.length - 1]));
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 8, GridLayoutManager.VERTICAL, false));
        }

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(folderPath.split("/")[folderPath.split("/").length - 1]);
    }

    @Override
    public void onItemClicked(int pos) {
        Toast.makeText(getContext(), "This feature is not available." + pos, Toast.LENGTH_SHORT).show();
    }
}