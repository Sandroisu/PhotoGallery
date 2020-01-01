package ru.alex.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    private List<GalleryItem> mItems = new ArrayList<>();
    int mPage = 2;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery,
                container, false);
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view);
        final GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(mGridLayoutManager);
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    new FetchItemsTask().execute(mPage);
                    mPage++;
                }

            }
        });
        setupAdapter();
        return v;

    }
    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
            mPhotoRecyclerView.scrollToPosition(mItems.size()-100);
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer,Void,List<GalleryItem>>{

        @Override
        protected List<GalleryItem> doInBackground(Integer... pageNum) {
          return new FlickrFetchr().fetchItems(pageNum[0]);

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems.addAll(galleryItems);
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTextView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }


        public void bindGalleryItem (GalleryItem item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
