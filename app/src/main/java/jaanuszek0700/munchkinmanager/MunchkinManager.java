package jaanuszek0700.munchkinmanager;

import android.app.Application;
import android.widget.ImageButton;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import jaanuszek0700.munchkinmanager.models.AppSettings;
import jaanuszek0700.munchkinmanager.models.Player;
import jaanuszek0700.munchkinmanager.utils.Migration;

public class MunchkinManager extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        // Konfiguracja produkcyjna
        // RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(1).migration(new Migration()).build();
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        // Czyszczenie bazy przed uruchomienim aplikacji
        // Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);


        Realm realm = Realm.getDefaultInstance();
        AppSettings appSettings = realm.where(AppSettings.class).findFirst();
        if(appSettings != null) {
            Player player = appSettings.getPlayer();
            realm.beginTransaction();
            player.setLevel(1);
            player.setInventory(0);
            player.setModifiers(0);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            appSettings = realm.createObject(AppSettings.class);
            Player player = realm.copyToRealm(new Player("Player", 1, 0, 0, "user_icon"));
            appSettings.setLanguage("en");
            appSettings.setPlayer(player);
            realm.commitTransaction();
        }
    }
}
