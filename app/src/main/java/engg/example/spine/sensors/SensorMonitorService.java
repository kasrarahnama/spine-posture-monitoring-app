package engg.example.spine.sensors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;


import androidx.annotation.Nullable;

import engg.example.spine.data.PostureLog;
import engg.example.spine.data.SettingsManager;
import engg.example.spine.data.SpineDatabase;
import engg.example.spine.util.NotificationHelper;

public class SensorMonitorService extends Service implements SensorEventListener {

    public static final String ACTION_POSTURE_UPDATE =
            "engg.example.spine.POSTURE_UPDATE";

    private SensorManager sensorManager;
    private Sensor accSensor;
    private Sensor gyroSensor;

    private final ComplementaryFilter filter = new ComplementaryFilter();
    private long lastTimestampNs = 0L;

    private float ax, ay, az;
    private float gxDeg, gyDeg, gzDeg;

    private String state = "GOOD";
    private long stateSinceMs = 0L;
    private long lastLoggedMs = 0L;

    private SettingsManager settings;
    private SpineDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationHelper.createChannels(this);
        Notification n = NotificationHelper.buildPersistent(this);
        startForeground(1, n);


        settings = new SettingsManager(this);
        db = SpineDatabase.get(getApplicationContext());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accSensor != null) {
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        if (gyroSensor != null) {
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        stateSinceMs = System.currentTimeMillis();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (lastTimestampNs == 0L) {
            lastTimestampNs = event.timestamp;
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gxDeg = (float) Math.toDegrees(event.values[0]);
            gyDeg = (float) Math.toDegrees(event.values[1]);
            gzDeg = (float) Math.toDegrees(event.values[2]);
        }

        float dt = (event.timestamp - lastTimestampNs) / 1_000_000_000f;
        lastTimestampNs = event.timestamp;
        if (dt <= 0f) return;


        filter.update(ax, ay, az, gxDeg, gyDeg, gzDeg, dt);
        float pitch = filter.getPitch();
        float roll = filter.getRoll();

        float dPitch = pitch - settings.getPitch0();
        float dRoll = roll - settings.getRoll0();


        evaluateState(dPitch, dRoll);
        maybeLog(pitch, roll, dPitch, dRoll);


        sendPostureUpdate(pitch, roll, dPitch, dRoll);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void evaluateState(float dPitch, float dRoll) {
        long now = System.currentTimeMillis();
        boolean bad = Math.abs(dPitch) > settings.getPitchThresh()
                || Math.abs(dRoll) > settings.getRollThresh();

        if (state.equals("GOOD")) {

            if (bad && now - stateSinceMs >= settings.getBadHoldSec() * 1000L) {
                state = "BAD";
                stateSinceMs = now;
                alertBadPosture();
            }
        } else if (state.equals("BAD")) {

            if (!bad && now - stateSinceMs >= 10_000L) {
                state = "GOOD";
                stateSinceMs = now;
            }
        }
    }

    private void alertBadPosture() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NotificationHelper.CH_ALERTS)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("Posture Alert")
                        .setContentText("Straighten your posture.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


        android.app.NotificationManager nm =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(1002, builder.build());
        }

        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vib != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(
                        400,
                        VibrationEffect.DEFAULT_AMPLITUDE
                ));
            } else {
                vib.vibrate(400);
            }
        }
    }
    private void maybeLog(float pitch, float roll, float dPitch, float dRoll) {
        long now = System.currentTimeMillis();
        if (now - lastLoggedMs >= 5000L) {
            lastLoggedMs = now;

            new Thread(() -> {
                PostureLog log = new PostureLog(now, pitch, roll, dPitch, dRoll, state);
                db.logDao().insert(log);
            }).start();
        }
    }

    private void sendPostureUpdate(float pitch, float roll, float dPitch, float dRoll) {
        long now = System.currentTimeMillis();
        long elapsedBadMs = state.equals("BAD")
                ? (now - stateSinceMs)
                : 0L;

        int badHoldMs = settings.getBadHoldSec() * 1000;
        int badProgress = 0;
        if (badHoldMs > 0 && elapsedBadMs > 0) {
            badProgress = (int) Math.min(100, (elapsedBadMs * 100L / badHoldMs));
        }

        Intent i = new Intent(ACTION_POSTURE_UPDATE);
        i.putExtra("pitch",    pitch);
        i.putExtra("roll",     roll);
        i.putExtra("dPitch",   dPitch);
        i.putExtra("dRoll",    dRoll);
        i.putExtra("state",    state);
        i.putExtra("badProgress", badProgress);
        sendBroadcast(i);
    }
}