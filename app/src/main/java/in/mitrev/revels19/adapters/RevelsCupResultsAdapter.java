package in.mitrev.revels19.adapters;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.sports.SportsResultModel;

public class RevelsCupResultsAdapter extends RecyclerView.Adapter<RevelsCupResultsAdapter.ResultViewHolder> {

    private List<SportsResultModel> resultsList;
    private Context context;

    public RevelsCupResultsAdapter(List<SportsResultModel> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_revels_cup_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        SportsResultModel result = resultsList.get(position);

        holder.eventName.setText(result.eventName);
        holder.eventRound.setText(result.eventRound);
//        holder.catLogo.setImageResource(new IconCollection().getIconResource(context,"sports"));
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView catLogo;
        TextView eventName;
        TextView eventRound;

        public ResultViewHolder(View itemView) {
            super(itemView);

            catLogo = itemView.findViewById(R.id.result_cat_logo_image_view);
            eventName = itemView.findViewById(R.id.res_event_name_text_view);
            eventRound = itemView.findViewById(R.id.res_round_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            View bottomSheetView = View.inflate(context, R.layout.dialog_results, null);
            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(bottomSheetView);

            TextView eventName = bottomSheetView.findViewById(R.id.result_dialog_event_name_text_view);
            eventName.setText(resultsList.get(getAdapterPosition()).eventName);

            TextView eventRound = bottomSheetView.findViewById(R.id.result_dialog_round_text_view);
            eventRound.setText(resultsList.get(getAdapterPosition()).eventRound);

            RecyclerView teamsRecyclerView = bottomSheetView.findViewById(R.id.result_dialog_teams_recycler_view);
            teamsRecyclerView.setAdapter(new SportsQualifiedTeamsAdapter(resultsList.get(getAdapterPosition()).eventResultsList, context));
            teamsRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

            dialog.show();
        }
    }

}
