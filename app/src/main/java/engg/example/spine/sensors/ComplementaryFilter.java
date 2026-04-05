package engg.example.spine.sensors;

public class ComplementaryFilter {

    private float pitchDeg = 0f;
    private float rollDeg = 0f;
    private static final float ALPHA = 0.98f;

    public void update(float ax, float ay, float az,
                       float gxDeg, float gyDeg, float gzDeg,
                       float dtSec) {


        float pitchAcc = (float) Math.toDegrees(
                Math.atan2(-ax, Math.sqrt(ay * ay + az * az))
        );
        float rollAcc = (float) Math.toDegrees(
                Math.atan2(ay, az)
        );


        pitchDeg = ALPHA * (pitchDeg + gxDeg * dtSec) + (1 - ALPHA) * pitchAcc;
        rollDeg  = ALPHA * (rollDeg  + gyDeg * dtSec) + (1 - ALPHA) * rollAcc;
    }

    public float getPitch() { return pitchDeg; }

    public float getRoll() { return rollDeg; }
}