package engg.example.spine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import engg.example.spine.data.PostureLog;
import engg.example.spine.data.SpineDatabase;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private SpineDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.lvHistory);
        db = SpineDatabase.get(getApplicationContext());

        loadLogs();
    }

    private void loadLogs() {
        new Thread(() -> {
            List<PostureLog> logs = db.logDao().latest(100);
            List<String> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            for (PostureLog log : logs) {
                String time = sdf.format(new Date(log.ts));
                String line = time + " | " + log.status +
                        String.format(" | pitch=%.1f roll=%.1f", log.pitch, log.roll);
                items.add(line);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        items
                );
                listView.setAdapter(adapter);
            });
        }).start();
    }
}