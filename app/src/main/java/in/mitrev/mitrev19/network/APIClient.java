package in.mitrev.mitrev19.network;

import in.mitrev.mitrev19.models.categories.CategoriesListModel;
import in.mitrev.mitrev19.models.events.EventsListModel;
import in.mitrev.mitrev19.models.events.ScheduleListModel;
import in.mitrev.mitrev19.models.results.ResultsListModel;
import in.mitrev.mitrev19.models.sports.SportsListModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class APIClient {
    private static Retrofit retrofit = null;

    private static final String BASE_URL = "https://api.mitrevels.in/";

    public static APIInterface getAPIInterface(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // Log.d(TAG,"Categories updated in background");

        }
        return retrofit.create(APIInterface.class);
    }
    public interface APIInterface{
        @GET("events")
        Call<EventsListModel> getEventsList();

        @GET("categories")
        Call<CategoriesListModel> getCategoriesList();

        @GET("results")
        Call<ResultsListModel> getResultsList();

        @GET("schedule")
        Call<ScheduleListModel> getScheduleList();

        @GET("sports")
        Call<SportsListModel> getSportsResults();

    }
}

