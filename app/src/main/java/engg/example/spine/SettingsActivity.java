package engg.example.spine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import engg.example.spine.data.SettingsManager;

public class SettingsActivity extends AppCompatActivity {

    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new SettingsManager(this);

        TextView tvPitch = findViewById(R.id.tvPitchThresh);
        TextView tvRoll = findViewById(R.id.tvRollThresh);
        TextView tvHold = findViewById(R.id.tvBadHold);

        SeekBar sbPitch = findViewById(R.id.sbPitchThresh);
        SeekBar sbRoll = findViewById(R.id.sbRollThresh);
        SeekBar sbHold = findViewById(R.id.sbBadHold);

        sbPitch.setMax(30);
        sbRoll.setMax(30);
        sbHold.setMax(60);

        sbPitch.setProgress((int) settings.getPitchThresh());
        sbRoll.setProgress((int) settings.getRollThresh());
        sbHold.setProgress(settings.getBadHoldSec());

        tvPitch.setText("Pitch threshold: " + sbPitch.getProgress() + "°");
        tvRoll.setText("Roll threshold: " + sbRoll.getProgress() + "°");
        tvHold.setText("Bad posture hold: " + sbHold.getProgress() + " s");

        sbPitch.setOnSeekBarChangeListener(new SimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.setPitchThresh(progress);
                tvPitch.setText("Pitch threshold: " + progress + "°");
            }
        });

        sbRoll.setOnSeekBarChangeListener(new SimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.setRollThresh(progress);
                tvRoll.setText("Roll threshold: " + progress + "°");
            }
        });

        sbHold.setOnSeekBarChangeListener(new SimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.setBadHoldSec(progress);
                tvHold.setText("Bad posture hold: " + progress + " s");
            }
        });
    }

    private abstract static class SimpleListener implements SeekBar.OnSeekBarChangeListener {
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}