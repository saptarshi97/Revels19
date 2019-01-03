package in.mitrev.revels19.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class EventDetailsModel extends RealmObject {

    @SerializedName("ename")
    @Expose
    private String eventName;
    @SerializedName("eid")
    @Expose
    private String eventID;
    @SerializedName("edesc")
    @Expose
    private String eventDesc;
    @SerializedName("emaxteamsize")
    @Expose
    private String eventMaxTeamSize;
    @SerializedName("cid")
    @Expose
    private String catId;
    @SerializedName("cname")
    @Expose
    private String catName;
    @SerializedName("cntctName")
    @Expose
    private String contactName;
    @SerializedName("cntctno")
    @Expose
    private String contactNo;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("day")
    @Expose
    private String day;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventMaxTeamSize() {
        return eventMaxTeamSize;
    }

    public void setEventMaxTeamSize(String eventMaxTeamSize) {
        this.eventMaxTeamSize = eventMaxTeamSize;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
