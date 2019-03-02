package in.mitrev.revels19.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.revels_live.RevelsLiveListModel;
import in.mitrev.revels19.models.revels_live.RevelsLiveModel;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    String TAG = "HomeAdapter";
    private RevelsLiveListModel feed;

    public HomeAdapter(RevelsLiveListModel feed) {
        this.feed = feed;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revels_live, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        RevelsLiveModel revelsLiveItem = feed.getRevelsLiveList().get(position);
        holder.onBind(revelsLiveItem);
    }

    @Override
    public int getItemCount() {
        return feed.getRevelsLiveList().size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView author;
        public TextView content;
        public TextView timestamps;

        public HomeViewHolder(View view) {
            super(view);
            initializeViews(view);
        }

        public void onBind(final RevelsLiveModel revelsLiveModel) {
            author.setText(revelsLiveModel.getAuthor());
            content.setText(revelsLiveModel.getContent());
            timestamps.setText(revelsLiveModel.getTimestamps());
            Picasso.get().load(revelsLiveModel.getImageURL()).into(image);
        }

        public void initializeViews(View view) {
            image = view.findViewById(R.id.revels_live_image_view);
            author = view.findViewById(R.id.author_text_view);
            content = view.findViewById(R.id.content_text_view);
            timestamps = view.findViewById(R.id.timestamp_text_view);
        }
    }
}
