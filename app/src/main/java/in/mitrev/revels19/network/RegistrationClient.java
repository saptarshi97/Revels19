package in.mitrev.revels19.network;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.mitrev.revels19.models.registration.CreateLeaveTeamResponse;
import in.mitrev.revels19.models.registration.LoginResponse;
import in.mitrev.revels19.models.registration.ProfileResponse;
import in.mitrev.revels19.models.registration.RegisteredEventListModel;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class RegistrationClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://register.mitrevels.in/";
    private static final String TAG = "RegistrationClient";
    private static final String COOKIE_NAME = "connect.sid";
    public static RegistrationInterface getRegistrationInterface(final Context context){
        if (retrofit == null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            //Storing the Cookie when receive in Response
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            Log.d(TAG, "saveFromResponse: cookie list size"+cookies.size());
                            for(Cookie cookie:cookies){
                                cookieStore.put(url, cookies);
                                if(cookie.name().contains(COOKIE_NAME)) {
                                    editor.putString("session_cookie", cookie.value());
                                    Log.d(TAG, "saveFromResponse: Putting Session cookie "+cookie.value());
                                }
                                else if (cookie.name().contains("__cfduid")){
                                    editor.putString("cloudflare_cookie", cookie.value());
                                    Log.d(TAG, "saveFromResponse: Putting cloudfarecookie "+cookie.value());
                                }
                            }
                            editor.apply();
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            //Returns Empty List
                            final ArrayList<Cookie> oneCookie = new ArrayList<>(0);
                            return oneCookie;
                        }
                    })
                    .addInterceptor(new LoggingInterceptor())
                    .build();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(RegistrationInterface.class);
    }
    public static String generateCookie(Context context){
        SharedPreferences  ck = PreferenceManager.getDefaultSharedPreferences(context);
        return "__cfduid="+ck.getString("cloudflare_cookie","")+";"+COOKIE_NAME+"="+ck.getString("session_cookie","");

    }
    public interface RegistrationInterface {
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("login")
        Call<LoginResponse> attemptLogin(@Body RequestBody body);

        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @GET("userProfile")
        Call<ProfileResponse> getProfileDetails(@Header("Cookie") String cookie);

        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @GET("registeredEvents")
        Call<RegisteredEventListModel> getRegisteredEvents(@Header("Cookie") String cookie);

        // To leave a team/deregister for an event
        @FormUrlEncoded
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("leaveteam")
        Call<CreateLeaveTeamResponse>leaveTeam(@Header("Cookie")String cookie, @Field("teamid")int teamID);

        //To register to an event
        @FormUrlEncoded
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("createteam")
        Call<CreateLeaveTeamResponse> createTeamResponse(@Header("Cookie") String cookie, @Field("eventid") int eventID);

        //Add a member to the team
        @FormUrlEncoded
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("addmember")
        Call<CreateLeaveTeamResponse> addMember(@Header("Cookie") String cookie, @Field("delid") String delID, @Field("eventid") int eventID);

    }

}
class LoggingInterceptor implements Interceptor {
    final boolean DEBUG = true;
    @Override
    public Response intercept(Chain chain) throws IOException {
        if(DEBUG)
            Log.d("LoggingInterceptor","inside intercept callback");
        Request request = chain.request();
        long t1 = System.nanoTime();
        String requestLog = String.format("Sending request %s on %s%n%s \n %s",
                request.url(), chain.connection(), request.body(), request.headers());
        if(request.method().compareToIgnoreCase("post")==0){
            requestLog ="\n"+requestLog+"\n"+bodyToString(request);
        }
        if(DEBUG)
            Log.d("TAG","request"+"\n"+requestLog);
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        String bodyString = response.body().string();

        if(DEBUG)
            Log.d("TAG","response only"+"\n"+bodyString);

        if(DEBUG)
            Log.d("TAG","response"+"\n"+responseLog+"\n"+bodyString);

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();

    }


    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}