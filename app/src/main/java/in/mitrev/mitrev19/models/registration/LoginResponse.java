package in.mitrev.mitrev19.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Saptarshi on 3/1/2019.
 */
public class LoginResponse {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("msg")
    @Expose
    private String message;

    public boolean getStatus() {
        return success;
    }

    public void setStatus(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
