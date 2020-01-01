package ru.alex.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "5efff69703cdd73ce6fe4cc429d270f8";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+
                        ": with "+urlSpec);
            }
            int bytesRead;
            byte [] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return  out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    public String getUrlString (String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    private void parseItems(List items, JSONObject jsonBody)
        throws IOException, JSONException{
        Gson gson = new Gson();
        Type galleyItemType = new TypeToken<ArrayList<GalleryItem>>(){}.getType();
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        String jsonPhotosString = photoJsonArray.toString();
        List<GalleryItem> galleyItemList =  gson.fromJson(jsonPhotosString,galleyItemType);
        items.addAll(galleyItemList);
      /*  for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();

            item.setmId(photoJsonObject.getString("id"));
            item.setmCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")){
                continue;
            }
            item.setmUrl(photoJsonObject.getString("url_s"));
            items.add(item);*/}






}
