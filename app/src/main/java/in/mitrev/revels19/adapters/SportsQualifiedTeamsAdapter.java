package in.mitrev.revels19.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.sports.SportsModel;

public class SportsQualifiedTeamsAdapter extends RecyclerView.Adapter<SportsQualifiedTeamsAdapter.QualifiedTeamViewHolder> {

    private List<SportsModel> resultsList;
    private Context context;

    public SportsQualifiedTeamsAdapter(List<SportsModel> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @NonNull
    @Override
    public QualifiedTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QualifiedTeamViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_qualified_teams, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QualifiedTeamViewHolder holder, int position) {
        SportsModel result = resultsList.get(position);
        holder.teamID.setText(result.getTeamID());
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public class QualifiedTeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamID;

        public QualifiedTeamViewHolder(View itemView) {
            super(itemView);
            teamID = itemView.findViewById(R.id.qualified_teams_id);
        }
    }
}
