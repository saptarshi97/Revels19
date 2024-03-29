package in.mitrev.mitrev19.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.activities.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    private final String NOTIFICATION_TITLE = "Upcoming Event";
    private final String LAUNCH_APPLICATION = "Launch Revels'19";
    private String notificationText = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
            String eventName = intent.getStringExtra("eventName");
            notificationText = eventName + " at " + intent.getStringExtra("startTime") + ", " + intent.getStringExtra("eventVenue");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int notifyId = 1;
                String channelId = "Revels_19";
                CharSequence name = "Revels Channel";
                NotificationChannel channel = new NotificationChannel(channelId, name,
                        NotificationManager.IMPORTANCE_HIGH);
                Notification notification = new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(NOTIFICATION_TITLE)
                        .setContentText(notificationText)
                        .setSmallIcon(R.drawable.ic_stat_excelsior_logo_lightbg_trans)
                        .setContentIntent(pendingIntent)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .addAction(new NotificationCompat.Action(0, LAUNCH_APPLICATION, pendingIntent))
                        .setChannelId(channelId)
                        .build();

                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(notifyId, notification);

            } else {
                Notification notify = new NotificationCompat.Builder(context)
                        .setContentTitle(NOTIFICATION_TITLE)
                        .setContentText(notificationText)
                        .setSmallIcon(R.drawable.ic_stat_excelsior_logo_lightbg_trans)
                        .setContentIntent(pendingIntent)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .addAction(new NotificationCompat.Action(0, LAUNCH_APPLICATION, pendingIntent))
                        .build();

                notificationManager.notify(Integer.parseInt(intent.getStringExtra("eventID")), notify);
            }
        }

    }
}
