package in.mitrev.revels19.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Revels19 extends Application {
    public static int searchOpen = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
