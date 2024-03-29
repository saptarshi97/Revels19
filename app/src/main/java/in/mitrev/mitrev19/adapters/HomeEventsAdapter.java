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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
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
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.EventViewHolder> {

    private List<ScheduleModel> events;
    private final EventClickListener eventClickListener;
    private Context context;
    FragmentActivity activity;
    private Realm mDatabase = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealm = mDatabase.where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = mDatabase.copyFromRealm(favouritesRealm);
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private final int EVENT_DAY_ZERO = 6;
    private final int EVENT_MONTH = Calendar.MARCH;
    private String TAG = "HomeEvntsAdp";

    public interface EventClickListener {
        void onItemClick(ScheduleModel event);
    }

    public HomeEventsAdapter(List<ScheduleModel> events, EventClickListener eventClickListener,
                             FragmentActivity activity) {
        this.events = events;
        this.activity = activity;
        this.eventClickListener = eventClickListener;
    }

    public interface EventLongPressListener {
        void onEventLongPress(ScheduleModel event);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        ScheduleModel event = events.get(position);
        holder.onBind(event);
        IconCollection icons = new IconCollection();
        holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventLogo;
        public TextView eventRound;
        public TextView eventName;
        public TextView eventTime;
        public RelativeLayout eventItem;

        public EventViewHolder(View view) {
            super(view);
            eventLogo = view.findViewById(R.id.fav_event_logo_image_view);
            eventRound = view.findViewById(R.id.fav_event_round_text_view);
            eventName = view.findViewById(R.id.fav_event_name_text_view);
            eventTime = view.findViewById(R.id.fav_event_time_text_view);
            eventItem = view.findViewById(R.id.fav_event_item);

        }

        public void onBind(final ScheduleModel event) {
            eventName.setText(event.getEventName());
            eventRound.setText("R" + event.getRound());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventTime.setVisibility(View.GONE);
            eventItem.setOnClickListener(v -> {
                if (eventClickListener != null) {
                    eventClickListener.onItemClick(event);
                }
                displayEventDialog(event, context);
            });
            eventItem.setOnLongClickListener(v -> {
                registerForEvent(event.getEventId());
                return true;
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

        public boolean isFavourite(ScheduleModel event) {
            FavouritesModel favourite = mDatabase.where(FavouritesModel.class)
                    .equalTo("id", event.getEventId()).equalTo("day", event.getDay())
                    .equalTo("round", event.getRound()).findFirst();
            return favourite != null;
        }

        private void displayEventDialog(final ScheduleModel event, Context context) {
            final View view = View.inflate(context, R.layout.event_dialog_info, null);
            final Dialog dialog = new Dialog(context);
            TabbedDialog td = new TabbedDialog();
            final String eventID = event.getEventId();
            final EventDetailsModel schedule = mDatabase.where(EventDetailsModel.class)
                    .equalTo("eventID", eventID).findFirst();
            TabbedDialog.EventFragment.DialogFavouriteClickListener fcl = add -> {
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

        private void addFavourite(ScheduleModel eventSchedule) {
            FavouritesModel favourite = new FavouritesModel();
            //Get Corresponding EventDetailsModel from Realm
            EventDetailsModel eventDetails = mDatabase.where(EventDetailsModel.class)
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
            favourite.setParticipants(eventDetails.getEventMaxTeamSize());
            favourite.setContactName(eventDetails.getContactName());
            favourite.setContactNumber(eventDetails.getContactNo());
            favourite.setCatName(eventDetails.getCatName());
            favourite.setDescription(eventDetails.getEventDesc());
            favourite.setIsRevels("1");
            //Commit to Realm
            mDatabase.beginTransaction();
            mDatabase.copyToRealm(favourite);
            mDatabase.commitTransaction();
            addNotification(eventSchedule, "1");
            favourites.add(favourite);
        }

        private void removeFavourite(ScheduleModel eventSchedule) {
            mDatabase.beginTransaction();
            mDatabase.where(FavouritesModel.class).equalTo("id", eventSchedule.getEventId())
                    .equalTo("day", eventSchedule.getDay()).findAll().deleteAllFromRealm();
            mDatabase.commitTransaction();

            for (int i = 0; i < favourites.size(); i++) {
                //Removing corresponding FavouritesModel from favourites
                FavouritesModel favourite = favourites.get(i);
                if ((favourite.getId().equals(eventSchedule.getEventId())) && (favourite.getDay()
                        .equals(eventSchedule.getDay()))) {
                    favourites.remove(favourite);
                }
            }
            removeNotification(eventSchedule);
        }

        private boolean favouritesContainsEvent(ScheduleModel eventSchedule) {
            for (FavouritesModel favourite : favourites) {
                //Checking if Corresponding Event exists
                if ((favourite.getId().equals(eventSchedule.getEventId()))
                        && (favourite.getDay().equals(eventSchedule.getDay()))) {
                    return true;
                }
            }
            return false;
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

        private void addNotification(ScheduleModel event, String isRevelsSTR) {
            Intent intent = new Intent(activity, NotificationReceiver.class);
            intent.putExtra("eventName", event.getEventName());
            intent.putExtra("startTime", getStartTimeFromTimestamp(event.getStartTime().substring(11, 16)));
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
                d = sdf.parse(getStartTimeFromTimestamp(event.getStartTime().substring(11, 16)));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            if (isRevelsSTR.contains("1")) {
                Log.d(TAG, "addNotification: inside notifications");
                int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 07th March
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

                Log.d(TAG, "addNotification: alarm set for calendar2");

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
            intent.putExtra("startTime", getStartTimeFromTimestamp(event.getStartTime().substring(11, 16)) );
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
    }
}