package in.mitrev.revels19.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.CategoryActivity;
import in.mitrev.revels19.models.categories.CategoryModel;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<CategoryModel> categoriesList;
    private Activity activity;

    public CategoriesAdapter(List<CategoryModel> categoriesList, Activity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_category,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        CategoryModel category = categoriesList.get(position);
        categoryViewHolder.catName.setText(category.getCategoryName());
        // TODO: Replace this
        categoryViewHolder.catLogo.setImageResource(R.mipmap.ic_launcher);

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        ImageView catLogo;
        TextView catName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            catLogo = itemView.findViewById(R.id.cat_event_logo_image_view);
            catName = itemView.findViewById(R.id.cat_event_name_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, CategoryActivity.class);
            intent.putExtra("id", categoriesList.get(getAdapterPosition()).getCategoryID());
            intent.putExtra("name", categoriesList.get(getAdapterPosition()).getCategoryName());
            intent.putExtra("description", categoriesList.get(getAdapterPosition()).getCategoryDescription());
            activity.startActivity(intent);
        }
    }
}
