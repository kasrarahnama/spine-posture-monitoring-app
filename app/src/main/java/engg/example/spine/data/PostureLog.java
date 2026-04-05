package engg.example.spine.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "posture_logs")
public class PostureLog {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long ts;
    public float pitch;
    public float roll;
    public float dPitch;
    public float dRoll;
    public String status;

    public PostureLog(long ts, float pitch, float roll,
                      float dPitch, float dRoll, String status) {
        this.ts = ts;
        this.pitch = pitch;
        this.roll = roll;
        this.dPitch = dPitch;
        this.dRoll = dRoll;
        this.status = status;
    }
}