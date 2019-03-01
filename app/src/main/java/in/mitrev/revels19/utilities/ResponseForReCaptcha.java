package in.mitrev.revels19.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;

/**
 * Created by Saptarshi on 3/1/2019.
 */
public class ResponseForReCaptcha {
    private static String uRT=null;
    public static String getResponseToken(Activity activity){
        SafetyNet.getClient(activity).verifyWithRecaptcha("6Lfho3IUAAAAAEu6JHZojPoo55KE885x5LJVIIfN")
                .addOnSuccessListener( activity,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was successful.
                                String userResponseToken=response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    uRT=userResponseToken;
                                }
                            }
                        })
                .addOnFailureListener( activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d("reCaptcha", "GCError: StatusCode " + statusCode);
                        } else {
                            // A unknown type of error occurred.
                            Log.d("reCaptcha", "GCError: " + e.getMessage());
                        }
                    }
                });
        return uRT;
    }
}
