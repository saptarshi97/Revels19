package in.mitrev.mitrev19.adapters;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.mitrev19.models.registration.CreateLeaveTeamResponse;
import in.mitrev.mitrev19.receivers.NotificationReceiver;
import in.mitrev.mitrev19.utilities.IconCollection;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.models.events.EventDetailsModel;
import in.mitrev.mitrev19.models.events.ScheduleModel;
import in.mitrev.mitrev19.models.favourites.FavouritesModel;
import in.mitrev.mitrev19.network.RegistrationClient;
import in.mitrev.mitrev19.views.TabbedDialog;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.EventViewHolder> {
    private final int EVENT_DAY_ZERO = 5;
    private final int EVENT_MONTH = Calendar.MARCH;
    private final EventClickListener eventListener;
    private final FavouriteClickListener favouriteListener;
    private final EventLongPressListener eventLongPressListener;
    String TAG = "ScheduleAdapter";
    Realm realm = Realm.getDefaultInstance();
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private FragmentActivity activity;
    private List<ScheduleModel> eventScheduleList;

    public interface EventClickListener {
        void onItemClick(ScheduleModel event, View view);
    }

    public interface FavouriteClickListener {
        void onItemClick(ScheduleModel event, boolean add);
    }

    public interface EventLongPressListener {
        void onItemLongPress(ScheduleModel event);
    }

    public ScheduleAdapter(FragmentActivity activity, List<ScheduleModel> events,
                           EventClickListener eventListener,
                           EventLongPressListener eventLongPressListener,
                           FavouriteClickListener favouriteListener) {
        this.eventScheduleList = events;
        this.eventListener = eventListener;
        this.favouriteListener = favouriteListener;
        this.eventLongPressListener = eventLongPressListener;
        this.activity = activity;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    public void updateList(List<ScheduleModel> eventScheduleList) {
        this.eventScheduleList.clear();
        this.eventScheduleList.addAll(eventScheduleList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        ScheduleModel event = eventScheduleList.get(position);
        holder.onBind(event, eventListener, eventLongPressListener, favouriteListener);
    }

    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }

    public boolean isFavourite(ScheduleModel event) {
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id",
                event.getEventId()).equalTo("day", event.getDay())
                .equalTo("round", event.getRound()).findFirst();
        return favourite != null;

    }

    private void addFavourite(ScheduleModel eventSchedule) {
        FavouritesModel favourite = new FavouritesModel();
        Log.i(TAG, "addFavourite: " + eventSchedule.getEventId());
        //Get Corresponding EventDetailsModel from Realm
        EventDetailsModel eventDetails = realm.where(EventDetailsModel.class)
                .equalTo("eventID", eventSchedule.getEventId()).findFirst();
        //Create and Set Values for FavouritesModel
        favourite.setId(eventSchedule.getEventId());
        favourite.setCatID(eventSchedule.getCatId());
        favourite.setEventName(eventSchedule.getEventName());
        favourite.setRound(eventSchedule.getRound());
        favourite.setVenue(eventSchedule.getVenue());
        favourite.setDate(eventSchedule.getDate());
        favourite.setDay(eventSchedule.getDay());
        favourite.setStartTime(eventSchedule.getStartTime());
        favourite.setEndTime(eventSchedule.getEndTime());
        favourite.setIsRevels("1");
        if (eventDetails != null) {
            favourite.setParticipants(eventDetails.getEventMaxTeamSize());
            favourite.setContactName(eventDetails.getContactName());
            favourite.setContactNumber(eventDetails.getContactNo());
            favourite.setCatName(eventDetails.getCatName());
            favourite.setDescription(eventDetails.getEventDesc());
        }
        //Commit to Realm
        if (realm != null) {
            realm.beginTransaction();
            realm.copyToRealm(favourite);
            realm.commitTransaction();
        }
        addNotification(eventSchedule, "1");

    }

    public void removeFavourite(ScheduleModel event) {
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("id", event.getEventId()).equalTo("day", event.getDay()).equalTo("round", event.getRound()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
    }

    private void addNotification(ScheduleModel event, String isRevelsSTR) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", (getStartTimeFromTimestamp(event.getStartTime().substring(11, 16))) );
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());
        intent.putExtra("catName", event.getCatName());
        Log.i("CategoryEventsAdapter", "addNotification: " + event.getStartTime());
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventId() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventId() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date d = null;
        try {
            d = sdf.parse((getStartTimeFromTimestamp(event.getStartTime().substring(11, 16))));
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        if (isRevelsSTR.contains("1")) {
            int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 06th March
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);
            calendar1.set(Calendar.MONTH, EVENT_MONTH);
            calendar1.set(Calendar.YEAR, 2019);
            calendar1.set(Calendar.DATE, eventDate);
            calendar1.set(Calendar.SECOND, 0);
            long eventTimeInMillis = calendar1.getTimeInMillis();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY) - 1);

            Calendar calendar2 = Calendar.getInstance();
            Log.d("Calendar 1", calendar1.getTimeInMillis() + "");
            Log.d("Calendar 2", calendar2.getTimeInMillis() + "");

            if (calendar2.getTimeInMillis() <= eventTimeInMillis)
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.SECOND, 0);
            calendar3.set(Calendar.MINUTE, 30);
            calendar3.set(Calendar.HOUR, 8);
            calendar3.set(Calendar.AM_PM, Calendar.AM);
            calendar3.set(Calendar.MONTH, Calendar.MARCH);
            calendar3.set(Calendar.YEAR, 2019);
            calendar3.set(Calendar.DATE, eventDate);
            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

                Log.d("Alarm", "set for " + calendar3.toString());
            }
        }
    }

    private void removeNotification(ScheduleModel event) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", (getStartTimeFromTimestamp(event.getStartTime().substring(11, 16))));
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventId() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventId() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }

    private String getDurationString(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf_24h = new SimpleDateFormat("H:mm", Locale.getDefault());
            int h=Integer.parseInt(startTime.substring(0,2));
            int m=Integer.parseInt(startTime.substring(3,5));
            if ( (m+30) >=60){
                m=m+30-60;
                h=h+1+5;
            }else {
                m=m+30;
                h+=5;
            }
            if(m<10)
                startTime=h+":0"+m;
            else
                startTime=h+":"+m;
            Date startDate = sdf_24h.parse(startTime) ;
            int he=Integer.parseInt(endTime.substring(0,2));
            int me=Integer.parseInt(endTime.substring(3,5));
            if ( (me+30) >=60){
                me=me+30-60;
                he=he+1+5;
            }else {
                me=me+30;
                he+=5;
            }
            if(me<10)
                endTime=he+":0"+me;
            else
                endTime=he+":"+me;
            Date endDate = sdf_24h.parse(endTime);
            SimpleDateFormat sdf_12h = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            startTime = sdf_12h.format(startDate);
            endTime = sdf_12h.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return startTime + " - " + endTime;
    }

    private void displayEventDialog(final ScheduleModel event, Context context) {
        final View view = View.inflate(context, R.layout.event_dialog_info, null);
        final Dialog dialog = new Dialog(context);
        TabbedDialog td = new TabbedDialog();
        final String eventID = event.getEventId();
        final EventDetailsModel schedule = realm.where(EventDetailsModel.class)
                .equalTo("eventID", eventID).findFirst();
        TabbedDialog.EventFragment.DialogFavouriteClickListener fcl =
                add -> {
                    //TODO: App crashes when snackbar is displayed(Currently commented out).Fix crash

                    if (add) {
                        addFavourite(event);
                        //Snackbar.make(view, event.getEventName()+" Added to Favourites", Snackbar.LENGTH_LONG).show();
                    } else {
                        removeFavourite(event);
                        //Snackbar.make(view, event.getEventName()+" removed from Favourites", Snackbar.LENGTH_LONG).show();
                    }
                    notifyDataSetChanged();
                };
        td.setValues(event, fcl, isFavourite(event), schedule);
        td.show(activity.getSupportFragmentManager(), "tag");
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventVenue, eventTime, eventRound, eventDate;
        public ImageView eventIcon, favIcon;
        public View container;

        public EventViewHolder(View view) {
            super(view);
            eventIcon = view.findViewById(R.id.event_logo_image_view);
            favIcon = view.findViewById(R.id.event_fav_icon);
            eventName = view.findViewById(R.id.event_name_text_view);
            eventDate = view.findViewById(R.id.event_date_text_view);
            eventVenue = view.findViewById(R.id.event_venue_text_view);
            eventTime = view.findViewById(R.id.event_time_text_view);
            eventRound = view.findViewById(R.id.event_round_text_view);
            container = view.findViewById(R.id.event_item_relative_layout);
        }

        public void onBind(final ScheduleModel event, final EventClickListener eventClickListener, final EventLongPressListener eventLongPressListener, final FavouriteClickListener favouriteListener) {
            eventDate.setText(event.getDate());
            eventName.setText(event.getEventName());
            String startTime = event.getStartTime().substring(11, 16);
            String endTime = event.getEndTime().substring(11, 16);
            String duration = getDurationString(startTime, endTime);
            eventTime.setText(duration);
            eventVenue.setText(event.getVenue());
            eventRound.setText("R".concat(event.getRound()));
            IconCollection icons = new IconCollection();
            eventIcon.setImageResource(icons.getIconResource(activity, event.getCatName()));
            if (isFavourite(event)) {
                favIcon.setImageResource(R.drawable.ic_fav_selected);
                favIcon.setTag("selected");
            } else {
                favIcon.setImageResource(R.drawable.ic_fav_deselected);
                favIcon.setTag("deselected");
            }
            favIcon.setOnClickListener(view -> {
                //Favourites Clicked
                String favTag = favIcon.getTag().toString();
                if (favTag.equals("deselected")) {
                    favIcon.setTag("selected");
                    favIcon.setImageResource(R.drawable.ic_fav_selected);
                    addFavourite(event);
                    favouriteListener.onItemClick(event, true);
                } else {
                    favIcon.setTag("deselected");
                    favIcon.setImageResource(R.drawable.ic_fav_deselected);
                    removeFavourite(event);
                    favouriteListener.onItemClick(event, false);
                }
            });
            container.setOnClickListener(view -> {
                Log.i(TAG, "onClick: Event clicked" + event.getEventName());
                displayEventDialog(event, view.getContext());

            });

            container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    registerForEvent(event.getEventId());
                    return true;
                }
            });
        }

        private void registerForEvent(String eventID) {
            Log.d(TAG, "registerForEvent: called");
            final ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setMessage("Trying to register you for event... please wait!");
            dialog.setCancelable(false);
            dialog.show();
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), "eventid=" + eventID);
            String cookie = RegistrationClient.generateCookie(activity);
            Call<CreateLeaveTeamResponse> call = RegistrationClient.getRegistrationInterface(activity)
                    .createTeamResponse(cookie, Integer.parseInt(eventID));
            call.enqueue(new Callback<CreateLeaveTeamResponse>() {
                @Override
                public void onResponse(Call<CreateLeaveTeamResponse> call, Response<CreateLeaveTeamResponse> response) {
                    dialog.cancel();
                    if (response != null && response.body() != null) {
                        try {
                            showAlert(response.body().getMsg());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        showAlert("Error! Please try again.");
                    }
                }

                @Override
                public void onFailure(Call<CreateLeaveTeamResponse> call, Throwable t) {
                    dialog.cancel();
                    showAlert("Error connecting to server! Please try again.");
                }
            });
        }

        public void showAlert(String message) {
            new AlertDialog.Builder(activity)
                    .setTitle("Alert")
                    .setIcon(R.drawable.ic_info)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {

                    }).show();
        }
    }
    private String getStartTimeFromTimestamp(String startTime) {
        try {
            SimpleDateFormat sdf_24h = new SimpleDateFormat("H:mm", Locale.getDefault());
            int h=Integer.parseInt(startTime.substring(0,2));
            int m=Integer.parseInt(startTime.substring(3,5));
            if ( (m+30) >=60){
                m=m+30-60;
                h=h+1+5;
            }else {
                m=m+30;
                h+=5;
            }
            if(m<10)
                startTime=h+":0"+m;
            else
                startTime=h+":"+m;
            Date startDate = sdf_24h.parse(startTime) ;
            SimpleDateFormat sdf_12h = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            startTime = sdf_12h.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return startTime;
    }

}