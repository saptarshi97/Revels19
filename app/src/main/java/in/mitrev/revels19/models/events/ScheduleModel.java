package in.mitrev.revels19.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.mitrev.revels19.models.categories.CategoryModel;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ScheduleModel extends RealmObject {

    @SerializedName("event")
    @Expose
    private String eventId;

    private String eventName;

    private String catId;

    private String catName;

    @SerializedName("round")
    @Expose
    private String round;

    @SerializedName("location")
    @Expose
    private String venue;

    private String isRevels;

    @SerializedName("start")
    @Expose
    private String startTime;

    @SerializedName("end")
    @Expose
    private String endTime;

    private String day;

    private String date;


    public ScheduleModel() {
        RealmQuery<CategoryModel> categoryQuery;
        RealmQuery<EventDetailsModel> eventQuery;
        Realm db = Realm.getDefaultInstance();
        eventQuery = db.where(EventDetailsModel.class);
        categoryQuery = db.where(CategoryModel.class);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        RealmQuery<EventDetailsModel> eventQuery;
        Realm db = Realm.getDefaultInstance();
        eventQuery = db.where(EventDetailsModel.class);
        eventName = eventQuery.equalTo("eventID", eventId)
                .findFirst().getEventName();
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCatId() {
        RealmQuery<EventDetailsModel> eventQuery;
        Realm db = Realm.getDefaultInstance();
        eventQuery = db.where(EventDetailsModel.class);
        catId = eventQuery.equalTo("eventID", eventId)
                .findFirst().getCatId();
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        RealmQuery<CategoryModel> categoryQuery;
        Realm db = Realm.getDefaultInstance();
        categoryQuery = db.where(CategoryModel.class);
        catName = categoryQuery.equalTo("categoryID", getCatId())
                .findFirst().getCategoryName();
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getIsRevels() {
        return "1";
    }

    public void setIsRevels(String isRevels) {
        this.isRevels = isRevels;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        switch (getDate()) {
            case "2019-02-26":
                day = "0";
                break;
            case "2019-02-27":
                day = "0";
                break;
            case "2019-02-28":
                day = "3";
                break;
            case "2019-02-29":
                day = "4";
                break;
            default:
                day = "0";
        }
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        date = startTime.substring(0, 11);
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

