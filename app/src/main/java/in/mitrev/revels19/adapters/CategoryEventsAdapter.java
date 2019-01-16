package in.mitrev.revels19.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.mitrev.revels19.R;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.events.EventModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.favourites.FavouritesModel;
import in.mitrev.revels19.views.TabbedDialog;
import io.realm.Realm;
import io.realm.RealmResults;


public class CategoryEventsAdapter extends
        RecyclerView.Adapter<CategoryEventsAdapter.CategoryEventsViewHolder> {

    private boolean isRevels;
    private Context context;
    private FragmentActivity activity;
    private List<EventModel> eventsList;
    EventModel event;
    private String TAG = CategoryEventsAdapter.class.getSimpleName();
    
    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealmResults = realm
            .where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favouritesList = realm.copyFromRealm(favouritesRealmResults);

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
    public void onBindViewHolder(@NonNull CategoryEventsViewHolder categoryEventsViewHolder, int position) {
        event = eventsList.get(position);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
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
            final Context context =  view.getContext();
            final Dialog dialog = new Dialog(context);
            TabbedDialog tabbedDialog = new TabbedDialog();
            final String eventID = event.getEventID();
            final String dayOfEvent = event.getDay();
            final EventDetailsModel schedule = realm.where(EventDetailsModel.class)
                    .equalTo("eventID", eventID).findFirst();
            if (schedule != null) {
                Log.d(TAG, "onClick: Using Schedule " + schedule.getDay());
            }
            ScheduleModel eventSchedule = realm.where(ScheduleModel.class)
                    .equalTo("eventID", eventID).equalTo("day", dayOfEvent)
                    .findFirst();
            //TabbedDialog
        }

    }

    //TODO: Methods to handle favourites and notifications
}
