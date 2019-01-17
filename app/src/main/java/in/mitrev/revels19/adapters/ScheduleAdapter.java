package in.mitrev.revels19.adapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.models.events.ScheduleModel;

public class ScheduleAdapter extends RecyclerView.Adapter {

    public ScheduleAdapter(FragmentActivity activity, List<ScheduleModel> events,
                           EventClickListener eventClickListener, EventLongPressListener eventLongPressListener,
                           FavouriteClickListener favouriteClickListener) {

    }

    public void updateList(List<ScheduleModel> eventScheduleList) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface EventClickListener {
        void onItemClick(ScheduleModel event, View view);
    }

    public interface FavouriteClickListener {
        void onItemClick(ScheduleModel event, boolean add);
    }

    public interface EventLongPressListener {
        void onItemLongPress(ScheduleModel event);
    }
}
