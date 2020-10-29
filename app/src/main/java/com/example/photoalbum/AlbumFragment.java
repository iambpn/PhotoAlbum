package com.example.photoalbum;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumFragment extends Fragment implements AlbumAdapter.ItemClicked {

    private AlbumAdapter adapter;
    private RecyclerView recyclerView;
    private String[] uniquePaths;
    private GetSetData getSetData;

    public interface GetSetData {
        String[] getUniquePaths();

        String getThumbnail(String folderPath, String folderName);

        void onAlbumSelected(int pos);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getSetData = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<AlbumData> albums = new ArrayList<>();
        adapter = new AlbumAdapter(getContext(), this, albums);
        recyclerView = view.findViewById(R.id.rv_album_recycler_view);
        uniquePaths = getSetData.getUniquePaths();
        String[] pathSplit;
        for (String path : uniquePaths) {
            pathSplit = path.split("/");
            albums.add(new AlbumData(pathSplit[pathSplit.length - 1], path, getSetData.getThumbnail(path, pathSplit[pathSplit.length - 1])));
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onItemClicked(int pos) {
        getSetData.onAlbumSelected(pos);
    }

}