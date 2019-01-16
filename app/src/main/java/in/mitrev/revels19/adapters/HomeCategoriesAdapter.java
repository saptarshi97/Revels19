package in.mitrev.revels19.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.CategoryActivity;
import in.mitrev.revels19.models.categories.CategoryModel;

/**
 * Created by Saptarshi on 12/24/2017.
 */


public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter. HomeViewHolder> {
    String TAG = "HomeCategoriesAdapter";
    private List<CategoryModel> categoriesList;
    private Context context;
    Activity activity;

    public HomeCategoriesAdapter(List<CategoryModel> categoriesList, Activity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder( HomeViewHolder holder, int position) {

    }
    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class  HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryLogo;
        public TextView categoryName;
        public RelativeLayout categoryItem;
        public HomeViewHolder(View view) {
            super(view);
            initializeViews(view);
        }

        public void onBind(final CategoryModel category) {

        }
        public void initializeViews(View view){

        }
    }
}