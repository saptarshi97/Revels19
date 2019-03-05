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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import in.mitrev.mitrev19.models.events.EventModel;
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


public class CategoryEventsAdapter extends
        RecyclerView.Adapter<CategoryEventsAdapter.CategoryEventsViewHolder> {
    private final int EVENT_DAY_ZERO = 25;
    private final int EVENT_MONTH = Calendar.FEBRUARY;
    private boolean isRevels;
    private Context context;
    private FragmentActivity activity;
    private List<EventModel> eventsList;
    EventModel event;
    private String TAG = CategoryEventsAdapter.class.getSimpleName();
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    
    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealmResults = realm
            .where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = realm.copyFromRealm(favouritesRealmResults);

    public CategoryEventsAdapter(List<EventModel> eventsList, FragmentActivity activity,
                                 Context context, boolean isRevels) {
        this.eventsList = eventsList;
        this.activity = activity;
        this.context = context;
        this.isRevels = isRevels;
    }

    @NonNull
    @Override
    public CategoryEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryEventsViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryEventsViewHolder holder, int position) {
        event = eventsList.get(position);

        holder.eventName.setText(event.getEventName());
        holder.eventTime.setText(getStartTimeFromTimestamp(event.getStartTime()
                .substring(11, 16)));
        IconCollection icons = new IconCollection();
        holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
        holder.eventRound.setVisibility(View.VISIBLE);

        if (event.getRound() != null && !event.getRound().equals("-") && !event.getRound().equals("")) {

            if (event.getRound().toLowerCase().charAt(0) == 'r')
                holder.eventRound.setText(event.getRound().toUpperCase());
            else {
                holder.eventRound.setText("R" + event.getRound().toUpperCase().charAt(0));
                Log.d(TAG, "onBindViewHolder: Round " + event.getRound().toUpperCase().charAt(0));
            }
        } else {
            holder.eventRound.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForEvent(event.getEventID());
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


    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public boolean isFavourite(EventModel event) {
        FavouritesModel favourite = realm.where(FavouritesModel.class)
                .equalTo("id", event.getEventID()).equalTo("day", event.getDay())
                .equalTo("round", event.getRound()).findFirst();
        return favourite != null;
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

    private void addFavourite(EventModel event) {
        FavouritesModel favourite = new FavouritesModel();
        List<ScheduleModel> scheduleModels = realm.where(ScheduleModel.class)
                .equalTo("eventId", event.getEventID()).findAll();
        ScheduleModel scheduleModel = null;
        for (int i = 0; i < scheduleModels.size(); i++) {
            if(scheduleModels.get(i).getDay().equals(event.getDay())) {
                scheduleModel = scheduleModels.get(i);
            }
        }
        favourite.setId(event.getEventID());
        favourite.setCatID(event.getCatId());
        favourite.setEventName(event.getEventName());
        favourite.setRound(event.getRound());
        favourite.setVenue(event.getVenue());
        favourite.setDate(event.getDate());
        favourite.setDay(event.getDay());
        favourite.setStartTime(event.getStartTime());
        favourite.setEndTime(event.getEndTime());
        favourite.setParticipants(event.getEventMaxTeamMember());
        favourite.setContactName(event.getContactName());
        favourite.setContactNumber(event.getContactNumber());
        favourite.setCatName(event.getCatName());
        favourite.setDescription(event.getEventDesc());
        favourite.setIsRevels("1");
        //Commit to Realm
        realm.beginTransaction();
        realm.copyToRealm(favourite);
        realm.commitTransaction();
        addNotification(event, scheduleModel.getIsRevels());
        favourites.add(favourite);
    }

    private void removeFavourite(EventModel event) {
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("eventName", event.getEventName())
                .equalTo("day", event.getDay()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
    }

    private void addNotification(EventModel event, String isRevelsSTR) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventID());
        intent.putExtra("catName", event.getCatName());
        Log.i("CategoryEventsAdapter", "addNotification: " + event.getStartTime());
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventID() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventID() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(event.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        if (isRevelsSTR.contains("1")) {
            int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 07th March
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);
            calendar1.set(Calendar.MONTH, EVENT_MONTH);
            calendar1.set(Calendar.YEAR, 2018);
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
            calendar3.set(Calendar.YEAR, 2018);
            calendar3.set(Calendar.DATE, eventDate);
            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

                Log.d("Alarm", "set for " + calendar3.toString());
            }
        }
    }

    private void removeNotification(EventModel event) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventID());
        Log.i(TAG, "removeNotification: " + event.getStartTime());
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventID() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventID() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }

    public class CategoryEventsViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        ImageView eventLogo;
        TextView eventName;
        TextView eventTime;
        FrameLayout logoFrame;
        TextView eventRound;

        public CategoryEventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventLogo = itemView.findViewById(R.id.cat_event_logo_image_view);
            eventName = itemView.findViewById(R.id.cat_event_name_text_view);
            eventTime = itemView.findViewById(R.id.cat_event_time_text_view);
            logoFrame = itemView.findViewById(R.id.fav_event_logo_frame);
            eventRound = itemView.findViewById(R.id.cat_event_round_text_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            final EventModel event = eventsList.get(getLayoutPosition());
            final Context context = view.getContext();
            final Dialog dialog = new Dialog(context);
            TabbedDialog td = new TabbedDialog();
            final String eventID = event.getEventID();
            final String dayOfEvent = event.getDay();
            final EventDetailsModel schedule = realm.where(EventDetailsModel.class)
                    .equalTo("eventID", eventID).findFirst();
            Log.d(TAG, "onClick: category" + schedule.getCatName());
            RealmResults<ScheduleModel> eventScheduleResults = realm.where(ScheduleModel.class)
                    .equalTo("eventId", eventID)
                    .findAll();
            List<ScheduleModel> eventScheduleList = realm.copyFromRealm(eventScheduleResults);
            ScheduleModel eventSchedule = new ScheduleModel();
            for (int i = 0; i < eventScheduleList.size(); i++) {
                ScheduleModel model = eventScheduleList.get(i);
                if (model.getDay().equals(dayOfEvent))
                    eventSchedule = model;
            }
            Log.d(TAG, "onClick: Using schedule" + eventSchedule.getEventName());
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
            td.setValues(eventSchedule, fcl, isFavourite(event), schedule);
            td.show(activity.getSupportFragmentManager(), "tag");
        }


    }

}
