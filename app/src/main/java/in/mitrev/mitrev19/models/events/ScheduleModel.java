package in.mitrev.mitrev19.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.mitrev.mitrev19.models.categories.CategoryModel;
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
        return eventQuery.equalTo("eventID", eventId)
                .findFirst().getEventName();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCatId() {
        RealmQuery<EventDetailsModel> eventQuery;
        Realm db = Realm.getDefaultInstance();
        eventQuery = db.where(EventDetailsModel.class);
        return eventQuery.equalTo("eventID", eventId)
                .findFirst().getCatId();
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        RealmQuery<CategoryModel> categoryQuery;
        Realm db = Realm.getDefaultInstance();
        categoryQuery = db.where(CategoryModel.class);
        return categoryQuery.equalTo("categoryID", getCatId())
                .findFirst().getCategoryName();
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
            case "06 Mar 2019":
                return "1";
            case "07 Mar 2019":
                return "2";
            case "08 Mar 2019":
                return "3";
            case "09 Mar 2019":
                return "4";
            default:
                return "1";
        }
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        String dateString = startTime.substring(0, 10);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
    }

    public void setDate(String date) {
        this.date = date;
    }
}

