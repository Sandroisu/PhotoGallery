package ru.alex.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE =
            "com.bignerdranch.android.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context){
        return new Intent (context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(!isNetworkAvailableAndConnected()){
            return;
        }

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if (query==null){
            items = new FlickrFetcher().fetchRecentPhotos();
        }else {
            items = new FlickrFetcher().searchPhotos(query);
        }
        if(items.size()==0){
            return;
        }

        String resultId = items.get(0).getmId();
        if (resultId.equals(lastResultId)){
            Log.i(TAG, "Got an old result: " + resultId);
        }else {
            Log.i(TAG, "Got a new result: " + resultId);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

            Notification notification = new Notification.Builder(this, "first")
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        QueryPreferences.setLastResultId(this, resultId);

    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = Objects.requireNonNull(cm).getActiveNetworkInfo()!=null;
        return Objects.requireNonNull(cm.getActiveNetworkInfo()).isConnected()&&isNetworkAvailable;
    }

    public  static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreferences.setAlarmOn(context, isOn);
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent i = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pendingIntent!=null;
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
}
