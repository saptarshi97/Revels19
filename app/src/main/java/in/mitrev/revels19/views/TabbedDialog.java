package in.mitrev.revels19.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager.widget.ViewPager;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.utilities.IconCollection;


public class TabbedDialog extends DialogFragment {
    ScheduleModel event;
    boolean favorite;
    EventFragment.DialogFavouriteClickListener favClickListener;
    EventDetailsModel schedule;
    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_event_details, container);
        mTabHost = view.findViewById(R.id.tabs);

        mTabHost.setup(getActivity(), getChildFragmentManager());
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Event"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Description"), Fragment.class, null);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        adapter = new PagerAdapter(getChildFragmentManager(), getArguments());

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mTabHost.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mTabHost.setOnTabChangedListener(s -> {
            int i = mTabHost.getCurrentTab();
            viewPager.setCurrentItem(i);
        });
        descriptionViewSet(view);
        return view;
    }

    public void descriptionViewSet(View view) {
        ImageView eventLogo1 = view.findViewById(R.id.event_logo_image_view);
        final TextView eventName = view.findViewById(R.id.event_name);
        ImageView deleteIcon = view.findViewById(R.id.event_delete_icon);
        deleteIcon.setVisibility(View.GONE);

        IconCollection icons = new IconCollection();
        try {
            eventLogo1.setImageResource(icons.getIconResource(getActivity(), event.getCatName()));
            eventName.setText(event.getEventName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ImageView favIcon = view.findViewById(R.id.event_fav_icon);
        favIcon.setOnClickListener(v -> {
            //FavIcon Clicked
            if (favIcon.getTag().equals("deselected")) {
                favIcon.setImageResource(R.drawable.ic_fav_selected);
                favIcon.setTag("selected");
                //Adding the favourite to the DB in EventsAdapter
                favClickListener.onItemClick(true);
            } else {
                favIcon.setImageResource(R.drawable.ic_fav_deselected);
                favIcon.setTag("deselected");
                //Removing the favourite from the DB in EventsAdapter
                favClickListener.onItemClick(false);
            }

        });
        if (favorite) {
            favIcon.setImageResource(R.drawable.ic_fav_selected);
            favIcon.setTag("selected");
        } else {
            favIcon.setImageResource(R.drawable.ic_fav_deselected);
            favIcon.setTag("deselected");
        }
    }

    public void setValues(ScheduleModel event, EventFragment.DialogFavouriteClickListener f,
                          boolean favorite, EventDetailsModel schedule) {
        this.event = event;
        this.favClickListener = f;
        this.favorite = favorite;
        this.schedule = schedule;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EventFragment extends Fragment {
        ScheduleModel event;
        boolean favorite;
        DialogFavouriteClickListener favClickListener;
        EventDetailsModel schedule;

        public EventFragment() {
            super();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = null;
            view = inflater.inflate(R.layout.event_dialog_info, container, false);
            if (event == null) {
                return view;
            }
            initViews(view);
            return view;
        }

        public void setExtras(ScheduleModel event, DialogFavouriteClickListener f, boolean favorite,
                              EventDetailsModel schedule) {
            this.event = event;
            this.favClickListener = f;
            this.favorite = favorite;
            this.schedule = schedule;

        }

        private String getDurationString(String startTime, String endTime) {
            try {
                SimpleDateFormat sdf_24h = new SimpleDateFormat("H:mm", Locale.getDefault());
                Date startDate = sdf_24h.parse(startTime);
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

        private void initViews(View view) {

            TextView eventRound = view.findViewById(R.id.event_round);
            eventRound.setText(event.getRound());

            TextView eventDate = view.findViewById(R.id.event_date);
            eventDate.setText(event.getDate());

            TextView eventTime = view.findViewById(R.id.event_time);
            String duration = getDurationString(event.getStartTime().substring(11, 16),
                    event.getEndTime().substring(11, 16));
            eventTime.setText(duration);

            TextView eventVenue = view.findViewById(R.id.event_venue);
            eventVenue.setText(event.getVenue());
            if (schedule != null) {
                TextView eventTeamSize = view.findViewById(R.id.event_team_size);
                eventTeamSize.setText(schedule.getEventMaxTeamSize());

                TextView eventContactName = view.findViewById(R.id.event_contact_name);
                eventContactName.setText(schedule.getContactName());

                TextView eventContact = view.findViewById(R.id.event_contact);
                eventContact.setText("(".concat(schedule.getContactNo()).concat(")"));
            }
            TextView eventCategory = view.findViewById(R.id.event_category);
            eventCategory.setText(event.getCatName());

            /*ImageView deleteIcon = (ImageView)view.findViewById(R.id.event_delete_icon);
            deleteIcon.setVisibility(View.GONE);*/
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        public interface DialogFavouriteClickListener {
            void onItemClick(boolean add);
        }
    }

    public static class DescriptionFragment extends Fragment {
        String description = "Description not available...";

        public DescriptionFragment() {
            super();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = null;
            view = inflater.inflate(R.layout.event_dialog_description, container, false);
            TextView eventDescription = view.findViewById(R.id.event_description);
            eventDescription.setText(description);
            return view;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        Bundle bundle;
        String[] titles;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public PagerAdapter(FragmentManager fm, Bundle bundle) {
            super(fm);
            this.bundle = bundle;
        }

        @NonNull
        @Override
        public Fragment getItem(int num) {
            Fragment fragment = null;
            if (num == 0) {
                EventFragment tf = new EventFragment();
                tf.setExtras(event, favClickListener, favorite, schedule);
                fragment = tf;
            } else {
                DescriptionFragment df = new DescriptionFragment();
                try {
                    df.setDescription(schedule.getEventDesc());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragment = df;
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
