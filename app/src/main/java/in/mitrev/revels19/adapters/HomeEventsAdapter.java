package in.mitrev.revels19.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.favourites.FavouritesModel;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Saptarshi on 12/24/2017.
 */

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.EventViewHolder> {

    private List<ScheduleModel> events;
    private final EventClickListener eventListener;
    private Context context;
    FragmentActivity activity;
    private Realm mDatabase = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealm = mDatabase.where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = mDatabase.copyFromRealm(favouritesRealm);
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private final int PRE_REVELS_DAY_ZERO = 18;
    private final int EVENT_DAY_ZERO = 6;
    private final int PRE_REVELS_EVENT_MONTH = Calendar.FEBRUARY;
    private final int EVENT_MONTH = Calendar.MARCH;
    private String TAG="HomeEvntsAdp";

    public interface EventClickListener {
        void onItemClick(ScheduleModel event);
    }
    public HomeEventsAdapter(List<ScheduleModel> events, EventClickListener eventListener, FragmentActivity activity) {
        this.events = events;
        this.activity = activity;
        this.eventListener = eventListener;
    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

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
        }
        public void onBind(final ScheduleModel event) {

//            eventName.setText(event.getEventName());
//            eventRound.setText("R" + event.getRound());
//            eventTime.setText(event.getStartTime()+" - "+ event.getEndTime());
//            eventTime.setVisibility(View.GONE);
//            eventItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(eventListener!=null){
//                        eventListener.onItemClick(event);
//                    }
//                    displayEventDialog(event, context);
////                    displayBottomSheet(event);
//                }
//            });
        }
        public boolean isFavourite(ScheduleModel event){
            return false;
        }

        private void displayEventDialog(final ScheduleModel event, Context context){

        }

        private void addFavourite(ScheduleModel eventSchedule){

        }

        private void removeFavourite(ScheduleModel eventSchedule){

        }

        private boolean favouritesContainsEvent(ScheduleModel eventSchedule){
            return false;
        }

        private void addNotification(ScheduleModel event, String isRevelsSTR) {

        }
        private void removeNotification(ScheduleModel event){

        }
    }
}