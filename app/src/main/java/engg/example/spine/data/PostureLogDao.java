package engg.example.spine.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostureLogDao {

    @Insert
    long insert(PostureLog log);

    @Query("SELECT * FROM posture_logs ORDER BY ts DESC LIMIT :limit")
    List<PostureLog> latest(int limit);
}