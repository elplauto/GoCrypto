package fr.elplauto.gocrypto.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leaderboard {

    @SerializedName("data")
    @Expose
    private List<LeaderboardRaw> leaderboardRaws = null;

    public List<LeaderboardRaw> getRaws() {
        return leaderboardRaws;
    }

    public void setData(List<LeaderboardRaw> leaderboardRaws) {
        this.leaderboardRaws = leaderboardRaws;
    }

}