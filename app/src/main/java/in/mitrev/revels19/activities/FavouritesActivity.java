package in.mitrev.revels19.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.adapters.FavouritesEventsAdapter;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.favourites.FavouritesModel;
import in.mitrev.revels19.receivers.NotificationReceiver;
import in.mitrev.revels19.views.TabbedDialog;
import io.realm.Realm;

public class FavouritesActivity extends AppCompatActivity {
    String TAG = "FavouritesActivity";
    RecyclerView recyclerViewDay1;
    RecyclerView recyclerViewDay2;
    RecyclerView recyclerViewDay3;
    RecyclerView recyclerViewDay4;
    Context context;
    private Realm realm;
    private List<FavouritesModel> favouritesDay1 = new ArrayList<>();
    private List<FavouritesModel> favouritesDay2 = new ArrayList<>();
    private List<FavouritesModel> favouritesDay3 = new ArrayList<>();
    private List<FavouritesModel> favouritesDay4 = new ArrayList<>();
    private TextView noEventsDay1;
    private TextView noEventsDay2;
    private TextView noEventsDay3;
    private TextView noEventsDay4;
    private TextView eventsClearDay1;
    private TextView eventsClearDay2;
    private TextView eventsClearDay3;
    private TextView eventsClearDay4;
    private FavouritesEventsAdapter adapterDay1;
    private FavouritesEventsAdapter adapterDay2;
    private FavouritesEventsAdapter adapterDay3;
    private FavouritesEventsAdapter adapterDay4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.favourites);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.favourites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        realm = Realm.getDefaultInstance();
        context = this;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSupportActionBar().setTitle(R.string.drawer_favourites);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                AppBarLayout appBarLayout = findViewById(R.id.app_bar);
                appBarLayout.setElevation(0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        favouritesDay1 = realm.copyFromRealm(realm.where(FavouritesModel.class).equalTo("day", "1").equalTo("isRevels", "1").findAll());
        favouritesDay2 = realm.copyFromRealm(realm.where(FavouritesModel.class).equalTo("day", "2").equalTo("isRevels", "1").findAll());
        favouritesDay3 = realm.copyFromRealm(realm.where(FavouritesModel.class).equalTo("day", "3").equalTo("isRevels", "1").findAll());
        favouritesDay4 = realm.copyFromRealm(realm.where(FavouritesModel.class).equalTo("day", "4").equalTo("isRevels", "1").findAll());
        displayEvents();
        eventsClearDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFavouriteCard(1);
            }
        });
        eventsClearDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFavouriteCard(2);
            }
        });
        eventsClearDay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFavouriteCard(3);
            }
        });
        eventsClearDay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFavouriteCard(4);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites_fragment, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_all: {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Favourites")
                        .setMessage("Are you sure you want to delete all favourites?")
                        .setIcon(R.drawable.ic_delete_all_dialog)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int favSize1 = favouritesDay1.size();
                                int favSize2 = favouritesDay2.size();
                                int favSize3 = favouritesDay3.size();
                                int favSize4 = favouritesDay4.size();
                                removeNotifications(favouritesDay1);
                                removeNotifications(favouritesDay2);
                                removeNotifications(favouritesDay3);
                                removeNotifications(favouritesDay4);
                                favouritesDay1.clear();
                                favouritesDay2.clear();
                                favouritesDay3.clear();
                                favouritesDay4.clear();
                                if (adapterDay1 != null) {
                                    adapterDay1.notifyItemRangeRemoved(0, favSize1);
                                }
                                if (adapterDay2 != null) {
                                    adapterDay2.notifyItemRangeRemoved(0, favSize2);
                                }
                                if (adapterDay3 != null) {
                                    adapterDay3.notifyItemRangeRemoved(0, favSize3);
                                }
                                if (adapterDay4 != null) {
                                    adapterDay4.notifyItemRangeRemoved(0, favSize4);
                                }
                                displayEvents();
                                updateRealm();
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, null).show();
                return true;
            }

            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void displayEvents() {

        recyclerViewDay1 = findViewById(R.id.favourites_day_1_recycler_view);
        recyclerViewDay2 = findViewById(R.id.favourites_day_2_recycler_view);
        recyclerViewDay3 = findViewById(R.id.favourites_day_3_recycler_view);
        recyclerViewDay4 = findViewById(R.id.favourites_day_4_recycler_view);

        noEventsDay1 = findViewById(R.id.fav_day_1_no_events);
        noEventsDay2 = findViewById(R.id.fav_day_2_no_events);
        noEventsDay3 = findViewById(R.id.fav_day_3_no_events);
        noEventsDay4 = findViewById(R.id.fav_day_4_no_events);


        eventsClearDay1 = findViewById(R.id.favourites_events_clear_1);
        eventsClearDay2 = findViewById(R.id.favourites_events_clear_2);
        eventsClearDay3 = findViewById(R.id.favourites_events_clear_3);
        eventsClearDay4 = findViewById(R.id.favourites_events_clear_4);

        FavouritesEventsAdapter.EventClickListener eventListener = new FavouritesEventsAdapter.EventClickListener() {
            @Override
            public void onItemClick(FavouritesModel event) {
                displayBottomSheet(event);
            }
        };

        if (favouritesDay1.isEmpty()) {
            recyclerViewDay1.setVisibility(View.GONE);
            ((View) recyclerViewDay1.getParent()).setVisibility(View.GONE);
            noEventsDay1.setVisibility(View.VISIBLE);
            ((View) noEventsDay1.getParent()).setVisibility(View.VISIBLE);
        } else {
            adapterDay1 = new FavouritesEventsAdapter(favouritesDay1, eventListener, this);
            recyclerViewDay1.setAdapter(adapterDay1);
            recyclerViewDay1.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay1.setNestedScrollingEnabled(false);
            recyclerViewDay1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if (favouritesDay2.isEmpty()) {
            recyclerViewDay2.setVisibility(View.GONE);
            ((View) recyclerViewDay2.getParent()).setVisibility(View.GONE);
            noEventsDay2.setVisibility(View.VISIBLE);
            ((View) noEventsDay2.getParent()).setVisibility(View.VISIBLE);
        } else {
            adapterDay2 = new FavouritesEventsAdapter(favouritesDay2, eventListener, this);
            recyclerViewDay2.setAdapter(adapterDay2);
            recyclerViewDay2.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay2.setNestedScrollingEnabled(false);
            recyclerViewDay2.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if (favouritesDay3.isEmpty()) {
            recyclerViewDay3.setVisibility(View.GONE);
            ((View) recyclerViewDay3.getParent()).setVisibility(View.GONE);
            noEventsDay3.setVisibility(View.VISIBLE);
            ((View) noEventsDay3.getParent()).setVisibility(View.VISIBLE);
        } else {
            adapterDay3 = new FavouritesEventsAdapter(favouritesDay3, eventListener, this);
            recyclerViewDay3.setAdapter(adapterDay3);
            recyclerViewDay3.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay3.setNestedScrollingEnabled(false);
            recyclerViewDay3.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if (favouritesDay4.isEmpty()) {
            recyclerViewDay4.setVisibility(View.GONE);
            ((View) recyclerViewDay4.getParent()).setVisibility(View.GONE);
            noEventsDay4.setVisibility(View.VISIBLE);
            ((View) noEventsDay4.getParent()).setVisibility(View.VISIBLE);
        } else {
            adapterDay4 = new FavouritesEventsAdapter(favouritesDay4, eventListener, this);
            recyclerViewDay4.setAdapter(adapterDay4);
            recyclerViewDay4.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay4.setNestedScrollingEnabled(false);
            recyclerViewDay4.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void displayBottomSheet(final FavouritesModel event) {
//        final View view = View.inflate(context, R.layout.event_dialog_info, null);
        final Dialog dialog = new Dialog(context);
        TabbedDialog td = new TabbedDialog();
        final String eventID = event.getId();
        final EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventID", eventID).findFirst();
        final ScheduleModel eventSchedule = realm.where(ScheduleModel.class).equalTo("eventID", eventID).equalTo("day", event.getDay()).findFirst();
        TabbedDialog.EventFragment.DialogFavouriteClickListener fcl = new TabbedDialog.EventFragment.DialogFavouriteClickListener() {
            @Override
            public void onItemClick(boolean add) {
                //TODO: App crashes when snackbar is displayed(Currently commented out).Fix crash

                if (add) {
                    addFavourite(event);
                    //Snackbar.make(view, event.getEventName()+" Added to Favourites", Snackbar.LENGTH_LONG).show();
                } else {
                    removeFavourite(event);
                    //Snackbar.make(view, event.getEventName()+" removed from Favourites", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        td.setValues(eventSchedule, fcl, isFavourite(event), schedule);
        td.show(getSupportFragmentManager(), "tag");

    }

    private void addFavourite(FavouritesModel eventSchedule) {
        FavouritesModel favourite = new FavouritesModel();
        Log.i(TAG, "addFavourite: " + eventSchedule.getId());
        //Get Corresponding EventDetailsModel from Realm
        EventDetailsModel eventDetails = realm.where(EventDetailsModel.class).equalTo("eventID", eventSchedule.getId()).equalTo("day", eventSchedule.getDay()).findFirst();
        //Create and Set Values for FavouritesModel
        favourite.setId(eventSchedule.getId());
        favourite.setCatID(eventSchedule.getCatID());
        favourite.setEventName(eventSchedule.getEventName());
        favourite.setRound(eventSchedule.getRound());
        favourite.setVenue(eventSchedule.getVenue());
        favourite.setDate(eventSchedule.getDate());
        favourite.setDay(eventSchedule.getDay());
        favourite.setStartTime(eventSchedule.getStartTime());
        favourite.setEndTime(eventSchedule.getEndTime());
        favourite.setIsRevels(eventSchedule.getIsRevels());
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
    }

    public void removeFavourite(FavouritesModel event) {
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("id", event.getId()).equalTo("day", event.getDay()).equalTo("round", event.getRound()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
        int day = getDay(event);
        switch (day) {
            case 1:
                favouritesDay1.remove(event);
                if (adapterDay1 != null) {
                    adapterDay1.notifyDataSetChanged();
                }
                break;
            case 2:
                favouritesDay2.remove(event);
                if (adapterDay2 != null) {
                    adapterDay2.notifyDataSetChanged();
                }
                break;
            case 3:
                favouritesDay3.remove(event);
                if (adapterDay3 != null) {
                    adapterDay3.notifyDataSetChanged();
                }
                break;
            case 4:
                favouritesDay4.remove(event);
                if (adapterDay4 != null) {
                    adapterDay4.notifyDataSetChanged();
                }
                break;
            default:

        }


    }

    public int getDay(FavouritesModel favouritesModel) {
        String day = favouritesModel.getDay();
        if (day.contains("1")) {
            return 1;
        } else if (day.contains("2")) {
            return 2;
        } else if (day.contains("3")) {
            return 3;
        } else if (day.contains("4")) {
            return 4;
        } else {
            return -1;
        }
    }

    public boolean isFavourite(FavouritesModel event) {
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id", event.getId()).equalTo("day", event.getDay()).equalTo("round", event.getRound()).findFirst();
        return favourite != null;

    }

    private void updateRealm() {
        realm.beginTransaction();
        realm.where(FavouritesModel.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(favouritesDay1);
        realm.copyToRealm(favouritesDay2);
        realm.copyToRealm(favouritesDay3);
        realm.copyToRealm(favouritesDay4);
        realm.commitTransaction();
    }

    private void removeNotification(FavouritesModel event) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getId());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatID() + event.getId() + "0");
        int RC2 = Integer.parseInt(event.getCatID() + event.getId() + "1");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }

    private void removeNotifications(List<FavouritesModel> events) {
        for (FavouritesModel event : events) {
            removeNotification(event);
        }
    }

    private void clearFavouriteCard(final int id) {
        String day = "";
        if (id == 0) {
            day = "Pre Revels";
        } else {
            day = "Day " + id;
        }
        new AlertDialog.Builder(context)
                .setTitle("Delete Favourites")
                .setMessage("Are you sure you want to delete all favourites from " + day + "?")
                .setIcon(R.drawable.ic_delete_all_dialog)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (id) {
                            case 1:
                                int favSize = favouritesDay1.size();
                                removeNotifications(favouritesDay1);
                                favouritesDay1.clear();
                                if (adapterDay1 != null) {
                                    adapterDay1.notifyItemRangeRemoved(0, favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 2:
                                favSize = favouritesDay2.size();
                                removeNotifications(favouritesDay2);
                                favouritesDay2.clear();
                                if (adapterDay2 != null) {
                                    adapterDay2.notifyItemRangeRemoved(0, favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 3:
                                favSize = favouritesDay3.size();
                                removeNotifications(favouritesDay3);
                                favouritesDay3.clear();
                                if (adapterDay3 != null) {
                                    adapterDay3.notifyItemRangeRemoved(0, favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 4:
                                favSize = favouritesDay4.size();
                                removeNotifications(favouritesDay4);
                                favouritesDay4.clear();
                                if (adapterDay4 != null) {
                                    adapterDay4.notifyItemRangeRemoved(0, favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_no, null).show();
    }
}