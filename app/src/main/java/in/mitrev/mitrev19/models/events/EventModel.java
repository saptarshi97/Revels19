package in.mitrev.mitrev19.models.events;

import io.realm.RealmObject;

public class EventModel extends RealmObject {

    private String date;
    private String day;
    private String eventName;
    private String eventID;
    private String eventDesc;
    private String eventMaxTeamMember;
    private String catName;
    private String catId;
    private String round;
    private String venue;
    private String startTime;
    private String endTime;
    private String hashTag;
    private String eventType;
    private String contactNumber;
    private String contactName;

    public EventModel() {}

    public EventModel(EventDetailsModel eventDetails, ScheduleModel schedule) {

        if(eventDetails != null) {
            eventName = eventDetails.getEventName();
            eventID = eventDetails.getEventID();
            eventDesc = eventDetails.getEventDesc();
            eventMaxTeamMember = eventDetails.getEventMaxTeamSize();
            catName = eventDetails.getCatName();
            catId = eventDetails.getCatId();
            contactName = eventDetails.getContactName();
            contactNumber = eventDetails.getContactNo();
            eventType = eventDetails.getType();
            //hashTag = eventDetails.getHash();
        }

        if(schedule != null) {
            venue = schedule.getVenue();

            if(schedule.getStartTime().contains(".")) {
                startTime = schedule.getStartTime().replace('.', ':');
            } else {
                startTime = schedule.getStartTime();
            }

            if(schedule.getEndTime().contains(".")) {
                endTime = schedule.getEndTime().replace('.', ':');
            } else {
                endTime = schedule.getEndTime();
            }

            day = schedule.getDay();
            date = schedule.getDate();
            round = schedule.getRound();
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public String getEventMaxTeamMember() {
        return eventMaxTeamMember;
    }

    public void setEventMaxTeamMember(String eventMaxTeamMember) {
        this.eventMaxTeamMember = eventMaxTeamMember;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
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

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
