package engg.example.spine.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CH_PERSIST = "spine_persist";
    public static final String CH_ALERTS = "spine_alerts";

    public static void createChannels(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);

            NotificationChannel chPersist = new NotificationChannel(
                    CH_PERSIST,
                    "SPINE Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            nm.createNotificationChannel(chPersist);

            NotificationChannel chAlerts = new NotificationChannel(
                    CH_ALERTS,
                    "SPINE Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(chAlerts);
        }
    }

    public static Notification buildPersistent(Context ctx) {
        return new NotificationCompat.Builder(ctx, CH_PERSIST)
                .setContentTitle("SPINE is running")
                .setContentText("Monitoring posture…")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setOngoing(true)
                .build();
    }
}