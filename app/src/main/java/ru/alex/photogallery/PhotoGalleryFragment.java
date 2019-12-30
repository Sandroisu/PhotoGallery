package ru.alex.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

public class PhotoGalleryFragment extends Fragment {
    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery,
                container, false);
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                3));
        return v;

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String result = new FlickrFetchr()
                        .getUrlString("https://bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: "+result);
            }catch (IOException ioe){
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            }
        return null;
        }
    }
}
