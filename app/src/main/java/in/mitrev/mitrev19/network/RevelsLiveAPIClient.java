package in.mitrev.mitrev19.network;

import java.util.concurrent.TimeUnit;

import in.mitrev.mitrev19.models.revels_live.RevelsLiveListModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class RevelsLiveAPIClient {

    public static final String BASE_URL_REVELS_LIVE = "https://revelslive.herokuapp.com";
    private static Retrofit retrofit = null;

    public static APIInterface getInterface(){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_REVELS_LIVE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit.create(APIInterface.class);
    }

    public interface APIInterface {

        @GET("/")
        Call<RevelsLiveListModel> getRevelsLiveFeed();

    }


}