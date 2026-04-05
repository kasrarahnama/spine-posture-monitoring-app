package engg.example.spine;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import engg.example.spine.data.SettingsManager;
import engg.example.spine.sensors.SensorMonitorService;
import engg.example.spine.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_NOTIF = 1001;

    private TextView tvStatus;
    private TextView tvAngles;
    private ProgressBar pbBadPosture;
    private SettingsManager settings;


    private final BroadcastReceiver postureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!SensorMonitorService.ACTION_POSTURE_UPDATE.equals(intent.getAction())) {
                return;
            }

            float pitch = intent.getFloatExtra("pitch", 0f);
            float roll  = intent.getFloatExtra("roll", 0f);
            String state = intent.getStringExtra("state");
            int badProgress = intent.getIntExtra("badProgress", 0);


            tvAngles.setText(String.format(
                    Locale.getDefault(),
                    "pitch: %.1f°, roll: %.1f°",
                    pitch, roll
            ));


            pbBadPosture.setProgress(badProgress);


            if ("BAD".equals(state)) {
                tvStatus.setText("Bad posture detected");
                tvStatus.setTextColor(Color.RED);
            } else {
                tvStatus.setText("SPINE is running");
                tvStatus.setTextColor(Color.GREEN);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        NotificationHelper.createChannels(this);

        setContentView(R.layout.activity_main);

        settings = new SettingsManager(this);

        tvStatus      = findViewById(R.id.tvStatus);
        tvAngles      = findViewById(R.id.tvAngles);
        pbBadPosture  = findViewById(R.id.pbBadPosture);

        Button btnStart     = findViewById(R.id.btnStart);
        Button btnStop      = findViewById(R.id.btnStop);
        Button btnCalibrate = findViewById(R.id.btnCalibrate);
        Button btnHistory   = findViewById(R.id.btnHistory);
        Button btnSettings  = findViewById(R.id.btnSettings);


        tvStatus.setText("SPINE is stopped");
        tvStatus.setTextColor(Color.DKGRAY);
        tvAngles.setText("pitch: 0°, roll: 0°");
        pbBadPosture.setProgress(0);

        btnStart.setOnClickListener(v -> {
            requestNotifPermissionIfNeeded();
            startSpineService();
        });

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, SensorMonitorService.class));
            tvStatus.setText("SPINE is stopped");
            tvStatus.setTextColor(Color.DKGRAY);
            pbBadPosture.setProgress(0);
            Toast.makeText(this, "SPINE stopped.", Toast.LENGTH_SHORT).show();
        });

        btnCalibrate.setOnClickListener(this::doCalibrate);

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter(SensorMonitorService.ACTION_POSTURE_UPDATE);
        registerReceiver(postureReceiver, f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(postureReceiver);
    }

    private void requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIF
                );
            }
        }
    }

    private void startSpineService() {
        Intent i = new Intent(this, SensorMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }

        tvStatus.setText("SPINE is running");
        tvStatus.setTextColor(Color.GREEN);
        Toast.makeText(
                this,
                "SPINE started. Monitoring posture...",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void doCalibrate(View v) {
        tvStatus.setText("Calibrating… keep good posture");
        tvStatus.setTextColor(Color.YELLOW);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }

            // ساده: baseline = 0
            settings.setBaseline(0f, 0f);

            runOnUiThread(() -> {
                Toast.makeText(
                        MainActivity.this,
                        "Calibrated (baseline = 0)",
                        Toast.LENGTH_SHORT
                ).show();
                tvStatus.setText("SPINE is running");
                tvStatus.setTextColor(Color.GREEN);
            });
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIF) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Notification permission granted.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Notifications may be limited.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}