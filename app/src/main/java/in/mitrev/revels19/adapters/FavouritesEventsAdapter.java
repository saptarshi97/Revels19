package in.mitrev.revels19.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.favourites.FavouritesModel;

public class FavouritesEventsAdapter extends RecyclerView.Adapter<FavouritesEventsAdapter.EventViewHolder> {

    private final EventClickListener eventListener;
    Activity activity;
    private List<FavouritesModel> favourites;
    private String TAG = "FavouritesEventsAdapter";

    public FavouritesEventsAdapter(List<FavouritesModel> favourites, EventClickListener eventListener, Activity activity) {
        this.favourites = favourites;
        this.eventListener = eventListener;
        this.activity = activity;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite_activity_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        FavouritesModel event = favourites.get(position);
        holder.onBind(event);
//        IconCollection icons = new IconCollection();
        Log.d(TAG, "onBindViewHolder: "
                + event.getCatName());
        try {
//            holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public interface EventClickListener {
        void onItemClick(FavouritesModel event);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventLogo;
        public TextView eventRound;
        public TextView eventName;
        public RelativeLayout eventItem;

        public EventViewHolder(View view) {
            super(view);
            eventLogo = view.findViewById(R.id.fav_event_logo_image_view);
            eventRound = view.findViewById(R.id.fav_event_round_text_view);
            eventName = view.findViewById(R.id.fav_event_name_text_view);
            eventItem = view.findViewById(R.id.fav_event_item);
        }

        public void onBind(final FavouritesModel event) {
            eventName.setText(event.getEventName());
            eventRound.setText("R".concat(event.getRound()));
            eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eventListener != null) {
                        eventListener.onItemClick(event);
                    }
                }
            });
        }
    }
}
