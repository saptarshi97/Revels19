package in.mitrev.revels19.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.results.ResultModel;

public class QualifiedTeamsAdapter extends RecyclerView.Adapter<QualifiedTeamsAdapter.QualifiedTeamViewHolder> {
    private List<ResultModel> resultsList;
    private Context context;

    public QualifiedTeamsAdapter(List<ResultModel> resultsList, Context context) {
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
        ResultModel result = resultsList.get(position);
        holder.teamID.setText(result.getTeamID());
        if (result.getRound().toLowerCase().contains("f") || result.getRound().toLowerCase().contains("final")) {
            holder.teamPosition.setVisibility(View.VISIBLE);
            holder.teamPosition.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.teamPosition.setText(result.getPosition() + ".");
        }

    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class QualifiedTeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamID;
        TextView teamPosition;

        public QualifiedTeamViewHolder(View itemView) {
            super(itemView);
            teamID = itemView.findViewById(R.id.qualified_teams_id);
            teamPosition = itemView.findViewById(R.id.qualified_team_position);

        }
    }
}