package in.mitrev.mitrev19.models.categories;

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

    @SerializedName("cc1_name")
    @Expose
    private String cc1_name;

    @SerializedName("cc1_contact")
    @Expose
    private String cc1_contact;

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCc1_name() {
        return cc1_name;
    }

    public void setCc1_name(String cc1_name) {
        this.cc1_name = cc1_name;
    }

    public String getCc1_contact() {
        return cc1_contact;
    }

    public void setCc1_contact(String cc1_contact) {
        this.cc1_contact = cc1_contact;
    }

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
