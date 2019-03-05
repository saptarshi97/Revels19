package in.mitrev.mitrev19.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisteredEventModel {

    @SerializedName("teamid")
    @Expose
    private int teamid;

    @SerializedName("event")
    @Expose
    private int event;

    @SerializedName("round")
    @Expose
    private int round;

    @SerializedName("delid")
    @Expose
    private int delid;

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getDelid() {
        return delid;
    }

    public void setDelid(int delid) {
        this.delid = delid;
    }
}
