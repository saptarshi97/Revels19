package in.mitrev.revels19.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private ProfileResponseData profileResponseData;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ProfileResponseData getProfileResponseData() {
        return profileResponseData;
    }

    public void setProfileResponseData(ProfileResponseData profileResponseData) {
        this.profileResponseData = profileResponseData;
    }
}
