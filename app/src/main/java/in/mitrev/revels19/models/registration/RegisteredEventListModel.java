package in.mitrev.revels19.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisteredEventListModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("data")
    @Expose
    private List<RegisteredEventModel> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<RegisteredEventModel> getData() {
        return data;
    }

    public void setData(List<RegisteredEventModel> data) {
        this.data = data;
    }

}

