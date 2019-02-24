package in.mitrev.revels19.network;

import android.util.Log;

import in.mitrev.revels19.models.categories.CategoriesListModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static android.content.ContentValues.TAG;


public class APIClient {
    private static Retrofit retrofit = null;

    private static final String BASE_URL = "https://api.mitrevels.in/";

    public static APIInterface getAPIInterface(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
           // Log.d(TAG,"Categornierusvniusbrejvbsejrvblserjvbies updated in background");

        }
        return retrofit.create(APIInterface.class);
    }
    public interface APIInterface{
        // @GET("events")
        // Call<EventsListModel> getEventsList();

        @GET("categories")
        Call<CategoriesListModel> getCategoriesList();

        // @GET("results")
        // Call<ResultsListModel> getResultsList();

        // @GET("schedule")
        // Call<ScheduleListModel> getScheduleList();

        // @GET("workshops")
        // Call<WorkshopsListModel> getWorkshopsList();

    }

}

