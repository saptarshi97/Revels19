package in.mitrev.mitrev19.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Revels19 extends Application {
    public static int searchOpen = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}