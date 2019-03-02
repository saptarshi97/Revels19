package in.mitrev.revels19.models.revels_live;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RevelsLiveListModel {

    @SerializedName("data")
    @Expose
    private List<RevelsLiveModel> revelsLiveList;

    public List<RevelsLiveModel> getRevelsLiveList() {
        return revelsLiveList;
    }

    public void setRevelsLiveList(List<RevelsLiveModel> revelsLiveList) {
        this.revelsLiveList = revelsLiveList;
    }

}
