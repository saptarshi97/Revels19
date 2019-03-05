package in.mitrev.mitrev19.models.results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.mitrev.mitrev19.models.categories.CategoryModel;
import in.mitrev.mitrev19.models.events.EventDetailsModel;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class ResultModel extends RealmObject {

    @SerializedName("teamid")
    @Expose
    private String teamID;

    private String catName;

    @SerializedName("event")
    @Expose
    private String eventID;

    private String eventName;
    @SerializedName("round")
    @Expose
    private String round;
    @SerializedName("position")
    @Expose
    private String position;

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getCatName() {
        Realm realm = Realm.getDefaultInstance();
        // Get Cat Id for this event
        RealmResults<EventDetailsModel> events = realm.where(EventDetailsModel.class).findAll();
        EventDetailsModel event = null;
        for (int i = 0; i < events.size(); i++) {
            EventDetailsModel temp = events.get(i);
            if (temp.getEventID().equals(eventID))
                event = temp;
        }
        String catId = event.getCatId();
        // Get the cat name for this event
        RealmResults<CategoryModel> categories = realm.where(CategoryModel.class).findAll();
        CategoryModel category = null;
        for (int i = 0; i < categories.size(); i++) {
            CategoryModel temp = categories.get(i);
            if (temp.getCategoryID().equals(catId))
                category = temp;
        }
        return category.getCategoryName();
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getEventName() {
        RealmQuery<EventDetailsModel> eventQuery;
        Realm db = Realm.getDefaultInstance();
        eventQuery = db.where(EventDetailsModel.class);
        eventName = eventQuery.equalTo("eventID", eventID)
                .findFirst().getEventName();
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}