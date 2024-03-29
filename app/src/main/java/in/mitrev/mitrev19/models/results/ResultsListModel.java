package in.mitrev.mitrev19.models.results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ResultsListModel {
    @SerializedName("data")
    @Expose
    private List<ResultModel> data = new ArrayList<>();

    public List<ResultModel> getData() {
        return data;
    }

    public void setData(List<ResultModel> data) {
        this.data = data;
    }

}
