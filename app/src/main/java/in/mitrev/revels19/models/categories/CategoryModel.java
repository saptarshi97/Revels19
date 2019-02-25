package in.mitrev.revels19.models.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class CategoryModel extends RealmObject {

    @SerializedName("id")
    @Expose
    private String categoryID;

    @SerializedName("name")
    @Expose
    private String categoryName;

    @SerializedName("type")
    @Expose
    private String categoryType;

    @SerializedName("description")
    @Expose
    private String categoryDescription;

    public CategoryModel() {
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return categoryType;
    }

    public void setType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
}
