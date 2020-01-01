package ru.alex.photogallery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryItem {
    @SerializedName("title")
    @Expose()
    private String mCaption;
    @SerializedName("id")
    @Expose()
    private String mId;
    @SerializedName("url_s")
    @Expose()
    private String mUrl;

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public String toString(){
        return mCaption;
    }
}
