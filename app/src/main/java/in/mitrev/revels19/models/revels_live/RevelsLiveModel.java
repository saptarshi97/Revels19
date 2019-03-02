package in.mitrev.revels19.models.revels_live;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RevelsLiveModel {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("imageURL")
    @Expose
    private String imageURL;

    @SerializedName("timestamps")
    @Expose
    private String timestamps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }
}
