package in.mitrev.revels19.fragments;


import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.FavouritesActivity;
import in.mitrev.revels19.activities.LoginActivity;
import in.mitrev.revels19.activities.ProfileActivity;
import in.mitrev.revels19.adapters.ScheduleAdapter;
import in.mitrev.revels19.application.Revels19;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.views.SwipeScrollView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private static final String TAG = ScheduleFragment.class.getSimpleName();
    private final int NUM_DAYS = 5;
    private final int PREREVELS_DAY = -1;
    private List<String> categoriesList = new ArrayList<>();
    private List<String> eventTypeList = new ArrayList<>();
    private List<String> venueList = new ArrayList<>();
    private List<ScheduleModel> currentDayEvents = new ArrayList<>();
    private List<ScheduleModel> filteredEvents = new ArrayList<>();
    private List<ScheduleModel> events = new ArrayList<>();
    private Realm realm;
    private ScheduleAdapter adapter;
    private String filterCategory = "All";
    private String filterVenue = "All";
    private String filterEventType = "All";
    private int filterStartHour = 12;
    private int filterStartMinute = 30;
    private int filterEndHour = 23;
    private int filterEndMinute = 59;
    private String[] sortCriteria = {"startTime", "eventName", "catName"};
    private Sort[] sortOrder = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
    private int tabNumber;
    private GestureDetector swipeDetector;
    private TabLayout tabs;
    private SwipeScrollView swipeScrollView;
    private View scheduleLayout;
    private RecyclerView scheduleRecyclerView;
    private LinearLayout noScheduleDataLayout;
    private View view;
    private View rootView;
    private MenuItem searchItem;
    private MenuItem filterItem;


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(){
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        categoriesList.add("All");
        eventTypeList.add("All");
        venueList.add("All");

        realm = Realm.getDefaultInstance();

        if (getActivity() != null) {
            getActivity().setTitle(R.string.bottom_nav_schedule);
            AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true, true);
            appBarLayout.setElevation(0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        rootView = view;
        initViews(view);

        events = realm.copyFromRealm(realm.where(ScheduleModel.class).findAll().sort(sortCriteria, sortOrder));

        if (events.size() != 0) {
            ScheduleAdapter.FavouriteClickListener favouriteClickListener = (event, add) -> {
                // Favourite Clicked
                if (add) {
                    Snackbar.make(view, event.getEventName() + " added to Favourites!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, event.getEventName() + " removed from Favourites!", Snackbar.LENGTH_SHORT).show();
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            };
            // Fetching list of Venues, Categories and Event names for the filter
            getAllCategories();
            getAllEvents();
            getAllVenues();

            // Binding the Events RecyclerView to the ScheduleAdapter
            adapter = new ScheduleAdapter(getActivity(), filteredEvents, null, null, favouriteClickListener);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            scheduleRecyclerView.setLayoutManager(layoutManager);
            scheduleRecyclerView.setItemAnimator(new DefaultItemAnimator());
            scheduleRecyclerView.setAdapter(adapter);
            // Setting current day
            setCurrentDay();
        }
        return view;
    }

    private void initViews(View view) {
        swipeDetector = new GestureDetector(view.getContext(), new SwipeListener());
        swipeScrollView = view.findViewById(R.id.schedule_swipe_scroll_view);
        swipeScrollView.setGestureDetector(swipeDetector);
        tabs = view.findViewById(R.id.tabs);
        scheduleLayout = view.findViewById(R.id.schedule_linear_layout);
        scheduleRecyclerView = view.findViewById(R.id.schedule_recycler_view);
        noScheduleDataLayout = view.findViewById(R.id.no_schedule_data_layout);
        tabs.addTab(tabs.newTab().setText(R.string.pre_revels));
        for (int i = 0; i < NUM_DAYS - 1; i++)
            tabs.addTab(tabs.newTab().setText("Day " + (i + 1)));
        DayTabListener tabListener = new DayTabListener();
        tabs.addOnTabSelectedListener(tabListener);
        scheduleRecyclerView.setOnTouchListener((v, event) -> {
            swipeDetector.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");
        searchItem = menu.findItem(R.id.action_search);
        filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(item -> {
            // Initializing Components

            View view = View.inflate(getActivity(), R.layout.dialog_filter, null);
            final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(view);

            LinearLayout clearFiltersLayout = view.findViewById(R.id.clear_filters_layout);
            LinearLayout startTimeLayout = view.findViewById(R.id.filter_start_time_layout);
            final TextView startTimeTextView = view.findViewById(R.id.start_time_text_view);

            LinearLayout endTimeLayout = view.findViewById(R.id.filter_end_time_layout);
            final TextView endTimeTextView = view.findViewById(R.id.end_time_text_view);

            TextView cancelTextView = view.findViewById(R.id.filter_cancel_button);
            TextView applyTextView = view.findViewById(R.id.filter_apply_button);

            final Spinner categorySpinner = view.findViewById(R.id.category_spinner);
            final Spinner venueSpinner = view.findViewById(R.id.event_venue_spinner);
            final Spinner eventTypeSpinner = view.findViewById(R.id.event_type_spinner);

            ArrayAdapter<String> categorySpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, categoriesList);
            categorySpinner.setAdapter(categorySpinnerAdapter);

            categorySpinner.setSelection(categoriesList.indexOf(filterCategory));

            ArrayAdapter<String> venueSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, venueList);
            venueSpinner.setAdapter(venueSpinnerAdapter);

            venueSpinner.setSelection(venueList.indexOf(filterVenue));

            ArrayAdapter<String> eventTypeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, eventTypeList);
            eventTypeSpinner.setAdapter(eventTypeSpinnerAdapter);

            eventTypeSpinner.setSelection(eventTypeList.indexOf(filterEventType));

            String sTime;
            String eTime;

            if (filterStartHour < 12)
                sTime = filterStartHour + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " AM";
            else if (filterStartHour == 12)
                sTime = filterStartHour + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " PM";
            else
                sTime = (filterStartHour - 12) + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " PM";

            if (filterEndHour < 12)
                eTime = filterEndHour + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " AM";
            else if (filterEndHour == 12)
                eTime = filterEndHour + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " PM";
            else
                eTime = (filterEndHour - 12) + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " PM";

            startTimeTextView.setText(sTime);
            endTimeTextView.setText(eTime);

            cancelTextView.setOnClickListener(v -> dialog.hide());

            applyTextView.setOnClickListener(v -> {
                applyFilters();
                dialog.hide();
            });

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    filterCategory = categorySpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    filterVenue = venueSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    filterEventType = eventTypeSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            startTimeLayout.setOnClickListener(v -> {
                TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
                    String startTime;
                    filterStartHour = hourOfDay;
                    filterStartMinute = minute;
                    if (hourOfDay < 12)
                        startTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " AM";
                    else if (hourOfDay == 12)
                        startTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " PM";
                    else
                        startTime = (hourOfDay - 12) + ":" + (minute < 10 ? "0" + minute : minute) + " PM";

                    startTimeTextView.setText(startTime);
                }, filterStartHour, filterStartMinute, false);

                tpDialog.show();
            });

            endTimeLayout.setOnClickListener(v -> {
                TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), (view12, hourOfDay, minute) -> {
                    String endTime;
                    filterEndHour = hourOfDay;
                    filterEndMinute = minute;
                    if (hourOfDay < 12)
                        endTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " AM";
                    else if (hourOfDay == 12)
                        endTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " PM";
                    else
                        endTime = (hourOfDay - 12) + ":" + (minute < 10 ? "0" + minute : minute) + " PM";

                    endTimeTextView.setText(endTime);
                }, filterEndHour, filterEndMinute, false);

                tpDialog.show();
            });

            clearFiltersLayout.setOnClickListener(view13 -> {
                dialog.hide();
                clearFilters();
                applyFilters();
                if (scheduleLayout != null)
                    Snackbar.make(rootView, "Filters cleared!", Snackbar.LENGTH_SHORT).show();
            });

            dialog.show();

            return false;

        });
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                queryFilter(text);
                Revels19.searchOpen = 2;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                queryFilter(text);
                Revels19.searchOpen = 2;
                return false;
            }
        });
        searchView.setQueryHint("Search..");
        searchView.setOnCloseListener(() -> {
            searchView.clearFocus();
            Revels19.searchOpen = 2;
            return false;
        });
    }

    private void clearFilters() {
        filterStartHour = 12;
        filterStartMinute = 30;
        filterEndHour = 23;
        filterEndMinute = 59;
        filterCategory = "All";
        filterVenue = "All";
        filterEventType = "All";
    }

    private void queryFilter(String query) {
        query = query.toLowerCase();
        List<ScheduleModel> temp = new ArrayList<>();
        for (int i = 0; i < currentDayEvents.size(); i++) {
            if ((currentDayEvents.get(i).getEventName().toLowerCase().contains(query) || currentDayEvents.get(i).getCatName().toLowerCase().contains(query))) {
                temp.add(currentDayEvents.get(i));
                Log.d(TAG, "queryFilter: " + currentDayEvents.get(i).getEventName());
            }
        }
        adapter.updateList(temp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (preferences.getBoolean("loggedIn", false)) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            case R.id.action_favourites:
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void getAllCategories() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll().sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String cat = scheduleResultList.get(i).getCatName();
            if (!categoriesList.contains(cat)) {
                categoriesList.add(cat);
            }
        }
    }

    private void getAllEvents() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll().sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String event = scheduleResultList.get(i).getEventName();
            if (!eventTypeList.contains(event)) {
                eventTypeList.add(event);
            }
        }
    }

    private void getAllVenues() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll().sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String venue = scheduleResultList.get(i).getVenue();
            if (!venueList.contains(venue)) {
                venueList.add(venue);
            }
        }
    }

    private void setCurrentDay() {
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2018, 2, 8);
        Calendar day2 = new GregorianCalendar(2018, 2, 9);
        Calendar day3 = new GregorianCalendar(2018, 2, 10);
        Calendar day4 = new GregorianCalendar(2018, 2, 11);
        Calendar curDay = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        if (curDay.getTimeInMillis() < day2.getTimeInMillis()) {
            tabNumber = 1;
        } else if (curDay.getTimeInMillis() < day3.getTimeInMillis()) {
            tabNumber = 2;
        } else if (curDay.getTimeInMillis() < day4.getTimeInMillis()) {
            tabNumber = 3;
        } else {
            tabNumber = 4;
        }
        try {
            TabLayout.Tab tabz = tabs.getTabAt(tabNumber);
            if (tabNumber == 0) {
                dayFilter(PREREVELS_DAY);
                applyFilters();
            } else {
                dayFilter(tabNumber + 1);
                applyFilters();
            }
            if (tabz != null) {
                tabz.select();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dayFilter(int day) {
        currentDayEvents.clear();
        // Filtering PreRevels events
        Log.d(TAG, "dayFilter 1: " + day);
        if (day == PREREVELS_DAY) {
            for (int i = 0; i < events.size(); i++) {
                Log.d(TAG, "dayFilter Value: " + events.get(i).getIsRevels());
                if (events.get(i).getIsRevels().contains("0")) {
                    currentDayEvents.add(events.get(i));
                }
            }
            if (adapter != null) {
                if (currentDayEvents.isEmpty()) {
                    scheduleRecyclerView.setVisibility(View.GONE);
                    noScheduleDataLayout.setVisibility(View.VISIBLE);
                } else {
                    scheduleRecyclerView.setVisibility(View.VISIBLE);
                    noScheduleDataLayout.setVisibility(View.GONE);
                }
                adapter.updateList(currentDayEvents);
            }
            return;
        }
        // Filtering the remaining events
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getDay().contains((day - 1) + "") && events.get(i).getIsRevels().contains("1")) {
                currentDayEvents.add(events.get(i));
            }
        }
        if (adapter != null) {
            if (currentDayEvents.isEmpty()) {
                scheduleRecyclerView.setVisibility(View.GONE);
                noScheduleDataLayout.setVisibility(View.VISIBLE);
            } else {
                scheduleRecyclerView.setVisibility(View.VISIBLE);
                noScheduleDataLayout.setVisibility(View.GONE);
            }
            adapter.updateList(currentDayEvents);
        }
    }

    private void applyFilters() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date startDate;
        Date endDate;
        // Adding all the events of the current day to the currentDayEvents List and filtering those
        // If this step is not done then the filtering is done on the list that has already been filtered
        if (tabs.getSelectedTabPosition() == 0) {
            dayFilter(PREREVELS_DAY);// PreRevels
        } else {
            dayFilter(tabs.getSelectedTabPosition() + 1);
        }
        List<ScheduleModel> tempList = new ArrayList<>(currentDayEvents);

        for (ScheduleModel event : currentDayEvents) {
            try {
                if (!filterCategory.equals("All") && !filterCategory.toLowerCase().equals(event.getCatName().toLowerCase())) {
                    // Filtering the category
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                if (!filterVenue.equals("All") && !event.getVenue().toLowerCase().contains(filterVenue.toLowerCase())) {
                    // Filtering based on venue
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                if (!filterEventType.equals("All") && !filterEventType.toLowerCase().equals(event.getEventName().toLowerCase())) {
                    // Filtering based on Event type
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                startDate = dateFormat.parse(event.getStartTime());
                endDate = dateFormat.parse(event.getEndTime());

                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                Calendar c3 = Calendar.getInstance();
                Calendar c4 = Calendar.getInstance();

                c1.setTime(startDate);
                c2.setTime(endDate);

                c3.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), filterStartHour,
                        filterStartMinute, c1.get(Calendar.SECOND));
                c3.set(Calendar.MILLISECOND, c1.get(Calendar.MILLISECOND));
                c4.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE), filterEndHour,
                        filterEndMinute, c2.get(Calendar.SECOND));
                c4.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));

                if (!((c1.getTimeInMillis() >= c3.getTimeInMillis()) && (c2.getTimeInMillis() <= c4.getTimeInMillis()))) {
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (tempList.isEmpty()) {
            if (view != null) {
                Snackbar.make(view, "No events found!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            if (view != null) {
                if (getArguments() != null) {
                    Snackbar.make(view, "Filters applied for Day " +
                            getArguments().getInt("day", 1) + "!", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
        if (adapter != null) {
            adapter.updateList(tempList);
        }
    }

    private class DayTabListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Log.d(TAG, "onTabSelected: TabPos : " + tab.getPosition());
            int day = tab.getPosition() + 1;
            if (tab.getPosition() == 0) {
                day = PREREVELS_DAY;
            }
            Log.d(TAG, "onTabSelected: day = " + day);
            dayFilter(day);
            applyFilters();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabUnselected: day = " + day);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            if (tab.getPosition() == 0) {
                day = PREREVELS_DAY;
            }
            Log.d(TAG, "onTabReselected: day = " + day);
            dayFilter(day);
            applyFilters();
        }
    }

    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_VERTICAL = 300;
        private static final int SWIPE_THRESHOLD_VELOCITY = 300;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: " + (Math.abs(e1.getY() - e2.getY())));

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_VERTICAL) {
                // Right to left Swipe
                Log.d(TAG, "onFling: RtoL Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if (!(tabIndex == NUM_DAYS - 1)) {
                    //Selecting the next tab
                    TabLayout.Tab t = tabs.getTabAt(tabIndex + 1);
                    if (t != null)
                        t.select();
                }
                return false;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_VERTICAL) {
                // Left to right Swipe
                Log.d(TAG, "onFling: LtoR Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if (!(tabIndex == 0)) {
                    //Selecting the previous tab
                    TabLayout.Tab t = tabs.getTabAt(tabIndex - 1);
                    if (t != null)
                        t.select();
                }
                return false;
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }
}
