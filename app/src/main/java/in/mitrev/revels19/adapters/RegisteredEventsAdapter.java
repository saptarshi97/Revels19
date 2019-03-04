package in.mitrev.revels19.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.registration.RegisteredEventModel;

/**
 * Created by Saptarshi on 3/4/2019.
 */
public class RegisteredEventsAdapter extends RecyclerView.Adapter<RegisteredEventsAdapter.RegisteredEventsViewHolder>{
    private List<RegisteredEventModel> regEventsList;
    private Context context;
    private List<EventDetailsModel> events;
    private RegActivityClickListener regActivityClickListener;
    public interface RegActivityClickListener{
        void onClick(Boolean isAdd, RegisteredEventModel regEvent);
    }


    public RegisteredEventsAdapter(List<RegisteredEventModel> regEventsList, List<EventDetailsModel> events,Context context, RegActivityClickListener regActivityClickListener) {
        this.regActivityClickListener=regActivityClickListener;
        this.regEventsList = regEventsList;
        this.context = context;
        this.events=events;
    }

    @Override
    public RegisteredEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new RegisteredEventsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_registered_events, parent, false));
    }

    @Override
    public void onBindViewHolder(RegisteredEventsViewHolder holder, int position) {
        RegisteredEventModel regEvent = regEventsList.get(position);
        for(EventDetailsModel event : events){
            if(regEvent.getEvent()== Integer.parseInt(event.getEventID()))
                holder.onBind(event,regEvent);
        }
    }

    @Override
    public int getItemCount() {
        return regEventsList.size();
    }


    class RegisteredEventsViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView teamID;
        LinearLayout addLeaveOps;
        Button addMember;
        Button leaveTeam;
        ImageView expand;
        public RegisteredEventsViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            teamID = itemView.findViewById(R.id.team_id);
            expand=itemView.findViewById(R.id.event_modify);
            addLeaveOps=itemView.findViewById(R.id.add_leave_ops);
            addMember=itemView.findViewById(R.id.add_member);
            leaveTeam=itemView.findViewById(R.id.leave_team);
        }
        public void onBind(final EventDetailsModel event,final RegisteredEventModel regEvent){
            eventName.setText(event.getEventName());
            teamID.setText(""+regEvent.getTeamid());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(addLeaveOps.getVisibility()==View.VISIBLE) {
                        expand.setRotation(0);
                        addLeaveOps.setVisibility(View.GONE);
                    }else{
                        expand.setRotation(180);
                        addLeaveOps.setVisibility(View.VISIBLE);
                    }
                }
            });
            leaveTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(regActivityClickListener!=null)
                        regActivityClickListener.onClick(false,regEvent);
                }
            });
            addMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(regActivityClickListener!=null)
                        regActivityClickListener.onClick(true,regEvent);
                }
            });

        }
    }
}
