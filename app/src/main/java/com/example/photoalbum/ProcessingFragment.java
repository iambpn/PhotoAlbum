package com.example.photoalbum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProcessingFragment extends Fragment {

    // the static fragment initialization parameters
    private static final String PHOTO_LOCATION = "photoLocation";
    private static final String PHOTO_NAME = "photoName";

    // Normal variable deceleration
    private String photoLocation;
    private String photoName;

    public ProcessingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ProcessingFragment newInstance(PhotoData photoData) {
        ProcessingFragment fragment = new ProcessingFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_LOCATION, photoData.getPhotoLocation());
        args.putString(PHOTO_NAME, photoData.getPhotoName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // check argument that is saved through args.putString()
            photoLocation = getArguments().getString(PHOTO_LOCATION);
            photoName = getArguments().getString(PHOTO_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imagePreview = view.findViewById(R.id.iv_image_preview);
        Picasso.get().load(new File(this.photoLocation)).into(imagePreview); // using Picasso dependency to view image
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(this.photoName);
    }
}