package engg.example.spine.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PostureLog.class}, version = 1, exportSchema = false)
public abstract class SpineDatabase extends RoomDatabase {

    private static volatile SpineDatabase INSTANCE;

    public abstract PostureLogDao logDao();

    public static SpineDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (SpineDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            ctx.getApplicationContext(),
                            SpineDatabase.class,
                            "spine.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}