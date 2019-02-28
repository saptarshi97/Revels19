package in.mitrev.revels19.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.mitrev.revels19.models.categories.CategoryModel;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class EventDetailsModel extends RealmObject {

    @SerializedName("name")
    @Expose
    private String eventName;
    @SerializedName("id")
    @Expose
    private String eventID;
    @SerializedName("short_desc")
    @Expose
    private String eventDesc;
    @SerializedName("max_size")
    @Expose
    private String eventMaxTeamSize;
    @SerializedName("category")
    @Expose
    private String catId;

    private String catName;

    private String contactName;

    private String contactNo;

    private String type;

    //private String hash;

    private String day;


    public EventDetailsModel() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<CategoryModel> eventQuery;
        eventQuery = db.where(CategoryModel.class);
    }

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
        Realm db = Realm.getDefaultInstance();
        RealmQuery<CategoryModel> eventQuery;
        eventQuery = db.where(CategoryModel.class);
        return eventQuery.equalTo("categoryID", catId)
                .findFirst()
                .getCategoryName();
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getContactName() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<CategoryModel> eventQuery;
        eventQuery = db.where(CategoryModel.class);
        return eventQuery.equalTo("categoryID", catId)
                .findFirst()
                .getCc1_name();
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<CategoryModel> eventQuery;
        eventQuery = db.where(CategoryModel.class);
        return eventQuery.equalTo("categoryID", catId)
                .findFirst()
                .getCc1_contact();
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getType() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<CategoryModel> eventQuery;
        eventQuery = db.where(CategoryModel.class);
        return eventQuery.equalTo("categoryID", catId)
                .findFirst()
                .getType();
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getHash() {
//        return hash;
//    }
//
//    public void setHash(String hash) {
//        this.hash = hash;
//    }

    public String getDay() {
        Realm db = Realm.getDefaultInstance();
        ScheduleModel scheduleResult = db.where(ScheduleModel.class)
                .equalTo("eventId", eventID).findFirst();
        return scheduleResult.getDay();
    }

    public void setDay(String day) {
        this.day = day;
    }
}
