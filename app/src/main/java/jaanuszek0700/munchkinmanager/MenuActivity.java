package jaanuszek0700.munchkinmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import io.realm.Realm;
import jaanuszek0700.munchkinmanager.models.AppSettings;
import jaanuszek0700.munchkinmanager.models.Player;

/**
 * Menu aplikacji
 */
public class MenuActivity extends FullscreenActivity {

    private static final int REQUEST_SETTINGS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private Player player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Realm realm = Realm.getDefaultInstance();
        final AppSettings appSettings = realm.where(AppSettings.class).findFirst();
        if(appSettings != null)
            setLanguage(appSettings.getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initializeContentView(findViewById(R.id.fullscreen_menu));

        if(appSettings != null)
            player = appSettings.getPlayer();

        TextView labelUsername = (TextView) findViewById(R.id.label_username);
        labelUsername.setText(player.getUsername());

        int imageIdentifier = getResources().getIdentifier(appSettings.getPlayer().getImage(), "drawable", "jaanuszek0700.munchkinmanager");
        if (imageIdentifier > 0)
            ((ImageView) findViewById(R.id.preferences_user_image)).setImageResource(imageIdentifier);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Windlass.ttf");
        ((Button) findViewById(R.id.button_create_room)).setTypeface(font);
        ((Button) findViewById(R.id.button_back)).setTypeface(font);
        labelUsername.setTypeface(font);

        // TODO można toprzenieść dalej, w dwa miejsca, przy wyszukiwaniu gry i przy tworzeniu pokoju
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // TODO: Dodać wyjaśnieni po co potrzebna jest lokalizacja
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // TODO zablokować przyciski gry, ewentualnie dodać tryb single player
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_SETTINGS:
                    recreate();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Otwiera okno preferencji
     *
     * @param vew widok
     */
    public void openPreferencesView(View vew) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    /**
     * Otwiera okno tworzenia nowego pokoju gry
     *
     * @param view widok
     */
    public void createNewGameRoom(View view) {
        Intent intent = new Intent(this, NewRoomActivity.class);
        startActivity(intent);
    }

    /**
     * Otwiera okno dołączania do pokoju
     *
     * @param view widok
     */
    public void joinGameRoom(View view) {
        Intent intent = new Intent(this, SelectRoomActivity.class);
        startActivity(intent);
    }

    private void setLanguage(String lang) {
        Locale myLocale = new Locale(lang);

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
