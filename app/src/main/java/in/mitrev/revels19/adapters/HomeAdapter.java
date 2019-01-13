package in.mitrev.revels19.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import in.mitrev.revels19.R;
import in.mitrev.revels19.models.instagram.InstaFeedModel;
import in.mitrev.revels19.models.instagram.InstagramFeed;

/**
 * Created by Saptarshi on 12/25/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter. HomeViewHolder> {
    String TAG = "HomeAdapter";
    private InstagramFeed feed;
    private Context context;
    public HomeAdapter(InstagramFeed feed) {
        this.feed = feed;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }
    @Override
    public void onBindViewHolder( HomeViewHolder holder, int position) {
        InstaFeedModel instaItem = feed.getFeed().get(position);
        holder.onBind(instaItem);
    }
    @Override
    public int getItemCount() {
        return feed.getFeed().size();
    }

    public class  HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView instaImage;
        public ImageView instaDP;
        public TextView instaName;
        public TextView instaDescription;
        public TextView instaLikes;
        public TextView instaComments;
        public LinearLayout instaItem;

        public  HomeViewHolder(View view) {
            super(view);
            initializeViews(view);
        }
        public void onBind(final InstaFeedModel instaItem) {

        }
        public void initializeViews(View view){

        }
    }
    public void launchInstagramImage(InstaFeedModel instaItem){
//        try {
//            Uri uri = Uri.parse(instaItem.getLink());
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            intent.setPackage("com.instagram.android");
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Log.e(TAG, e.getMessage()+"\n Perhaps user does not have Instagram installed ");
//            //Launching in Browser
//            try {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instaItem.getLink()));
//                context.startActivity(browserIntent);
//            }catch(ActivityNotFoundException e2){
//                Log.e(TAG, e2.getMessage()+"\n Perhaps user does not have Instagram installed ");
//            }
//        }
    }
    public void launchInstagramUser(InstaFeedModel instaItem){
//        String userURL = "https://instagram.com/_u/"+instaItem.getUser().getUsername().toString();
//        try {
//            Uri uri = Uri.parse(userURL);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            intent.setPackage("com.instagram.android");
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Log.e(TAG, e.getMessage() + "\n Perhaps user does not have Instagram installed ");
//            //Launching in Browser
//            try {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(userURL));
//                context.startActivity(browserIntent);
//            }catch(ActivityNotFoundException e2){
//                Log.e(TAG, e2.getMessage()+"\n Perhaps user does not have a Browser installed ");
//            }
//        }
    }
}