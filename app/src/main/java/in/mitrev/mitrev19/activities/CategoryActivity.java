package in.mitrev.mitrev19.activities;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.adapters.CategoryEventsAdapter;
import in.mitrev.mitrev19.models.events.EventDetailsModel;
import in.mitrev.mitrev19.models.events.EventModel;
import in.mitrev.mitrev19.models.events.ScheduleModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = CategoryActivity.class.getSimpleName();
    private String categoryName;
    private String categoryID;
    private String categoryDesc;
    private Realm database;
    private TextView catNameTextView;
    private TextView catDescTextView;
    private TextView noEventsDay1TextView;
    private TextView noEventsDay2TextView;
    private TextView noEventsDay3TextView;
    private TextView noEventsDay4TextView;
    private List<ScheduleModel> scheduleResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        categoryName = getIntent().getStringExtra("name");
        categoryID = getIntent().getStringExtra("id");
        categoryDesc = getIntent().getStringExtra("description");
        Log.d(TAG, "onCreate: " + categoryName);
        if (categoryName == null) categoryName = "";
        if (categoryID == null) categoryID = "";
        if (categoryDesc == null) categoryDesc = "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                setTitle(R.string.about_us);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(categoryName);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        database = Realm.getDefaultInstance();
        displayEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.about_category:
                View view = View.inflate(this, R.layout.dialog_about_category, null);
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                catNameTextView = view.findViewById(R.id.category_about_name);
                catDescTextView = view.findViewById(R.id.category_about_description);
                catNameTextView.setText(categoryName);
                catDescTextView.setText(categoryDesc);
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayEvents() {
        Log.d(TAG, "displayEvents: Hello");
        List<EventModel> day1List = new ArrayList<>();
        List<EventModel> day2List = new ArrayList<>();
        List<EventModel> day3List = new ArrayList<>();
        List<EventModel> day4List = new ArrayList<>();

        noEventsDay1TextView = findViewById(R.id.cat_day_1_no_events_text_view);
        noEventsDay2TextView = findViewById(R.id.cat_day_2_no_events_text_view);
        noEventsDay3TextView = findViewById(R.id.cat_day_3_no_events_text_view);
        noEventsDay4TextView = findViewById(R.id.cat_day_4_no_events_text_view);

        if(database == null)
            return;

        RealmResults<ScheduleModel> scheduleModelRealmResults = database.where(ScheduleModel.class)
                .findAll().sort("startTime");
        List<ScheduleModel> tempscheduleResults = database.copyFromRealm(scheduleModelRealmResults);
        for (int i = 0; i < scheduleModelRealmResults.size(); i++) {
            ScheduleModel scheduleModel = tempscheduleResults.get(i);
            if(scheduleModel.getCatId().equals(categoryID))
                scheduleResults.add(scheduleModel);
        }

        Log.d(TAG, "displayEvents: categoryId" + categoryID);
        Log.d(TAG, "displayEvents: no of events " + scheduleResults.size());

        for(ScheduleModel schedule : scheduleResults) {
            if (schedule.getDay().contains("0")) {
                Log.d(TAG, "displayEvents: PreRevels");
                EventDetailsModel eventDetails = database.where(EventDetailsModel.class)
                        .equalTo("eventID", schedule.getEventId()).findFirst();
                EventModel event = new EventModel(eventDetails, schedule);
            } else {
                Log.d(TAG, "displayEvents: Revels");
                EventDetailsModel eventDetails = database.where(EventDetailsModel.class)
                        .equalTo("eventID", schedule.getEventId()).findFirst();
                EventModel event = new EventModel(eventDetails, schedule);
                Log.d(TAG, "displayEvents: " + event.getEventName() + " " + event.getDay());
                switch (event.getDay()) {
                    case "1":
                        day1List.add(event);
                        break;
                    case "2":
                        day2List.add(event);
                        break;
                    case "3":
                        day3List.add(event);
                        break;
                    case "4":
                        day4List.add(event);
                        break;
                }
            }
        }
        eventSort(day1List);
        eventSort(day2List);
        eventSort(day3List);
        eventSort(day4List);


        RecyclerView recyclerViewDay1 = findViewById(R.id.cat_day_1_recycler_view);
        if(day1List.isEmpty()) {
            noEventsDay1TextView.setVisibility(View.VISIBLE);
            recyclerViewDay1.setVisibility(View.GONE);
        } else {
            recyclerViewDay1.setAdapter(new CategoryEventsAdapter(day1List, this,
                    getBaseContext(), false));
            recyclerViewDay1.setNestedScrollingEnabled(false);
            recyclerViewDay1.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }

        RecyclerView recyclerViewDay2 = findViewById(R.id.cat_day_2_recycler_view);
        if(day2List.isEmpty()) {
            noEventsDay2TextView.setVisibility(View.VISIBLE);
            recyclerViewDay2.setVisibility(View.GONE);
        } else {
            recyclerViewDay2.setAdapter(new CategoryEventsAdapter(day2List, this,
                    getBaseContext(), false));
            recyclerViewDay2.setNestedScrollingEnabled(false);
            recyclerViewDay2.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }

        RecyclerView recyclerViewDay3 = findViewById(R.id.cat_day_3_recycler_view);
        if(day3List.isEmpty()) {
            noEventsDay3TextView.setVisibility(View.VISIBLE);
            recyclerViewDay3.setVisibility(View.GONE);
        } else {
            recyclerViewDay3.setAdapter(new CategoryEventsAdapter(day3List, this,
                    getBaseContext(), false));
            recyclerViewDay3.setNestedScrollingEnabled(false);
            recyclerViewDay3.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }

        RecyclerView recyclerViewDay4 = findViewById(R.id.cat_day_4_recycler_view);
        if(day4List.isEmpty()) {
            noEventsDay4TextView.setVisibility(View.VISIBLE);
            recyclerViewDay4.setVisibility(View.GONE);
        } else {
            recyclerViewDay4.setAdapter(new CategoryEventsAdapter(day4List, this,
                    getBaseContext(), false));
            recyclerViewDay4.setNestedScrollingEnabled(false);
            recyclerViewDay4.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void eventSort(List<EventModel> eventsList) {
        Collections.sort(eventsList, (o1, o2) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);

            try {
                Date d1 = simpleDateFormat.parse(o1.getDay());
                Date d2 = simpleDateFormat.parse(o2.getDay());

                Calendar c1 = Calendar.getInstance();
                c1.setTime(d1);
                Calendar c2 = Calendar.getInstance();
                c2.setTime(d2);

                long diff = c1.getTimeInMillis() - c2.getTimeInMillis();
                if(diff > 0) return 1;
                else if(diff < 0) return -1;
                else {
                    int catDiff = o1.getCatName().compareTo(o2.getCatName());

                    if(catDiff > 0) return 1;
                    else if(catDiff < 0) return -1;
                    else {
                        int eventDiff = o1.getEventName().compareTo(o2.getEventName());

                        if(eventDiff > 0) return 1;
                        else if(eventDiff < 0) return -1;
                        else return 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
    }
}
