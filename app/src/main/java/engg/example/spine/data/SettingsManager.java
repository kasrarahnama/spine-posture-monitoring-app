package engg.example.spine.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREF = "spine_settings";
    private final SharedPreferences sp;

    public SettingsManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public float getPitchThresh() { return sp.getFloat("pitchThresh", 12f); }
    public float getRollThresh()  { return sp.getFloat("rollThresh", 10f); }
    public int getBadHoldSec()    { return sp.getInt("badHoldSec", 15); }
    public int getInactHoldMin()  { return sp.getInt("inactHoldMin", 30); }

    public void setPitchThresh(float v) {
        sp.edit().putFloat("pitchThresh", v).apply();
    }

    public void setRollThresh(float v) {
        sp.edit().putFloat("rollThresh", v).apply();
    }

    public void setBadHoldSec(int v) {
        sp.edit().putInt("badHoldSec", v).apply();
    }

    public void setInactHoldMin(int v) {
        sp.edit().putInt("inactHoldMin", v).apply();
    }


    public void setBaseline(float pitch0, float roll0) {
        sp.edit()
                .putFloat("pitch0", pitch0)
                .putFloat("roll0", roll0)
                .apply();
    }

    public float getPitch0() { return sp.getFloat("pitch0", 0f); }
    public float getRoll0()  { return sp.getFloat("roll0", 0f); }
}