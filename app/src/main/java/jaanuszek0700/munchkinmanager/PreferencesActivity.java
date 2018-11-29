package jaanuszek0700.munchkinmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import jaanuszek0700.munchkinmanager.models.AppSettings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PreferencesActivity extends FullscreenActivity {

    public static final int REQUEST_USER_IMAGE = 1;
    public static final String USER_IMAGE = "user_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        initializeContentView(findViewById(R.id.fullscreen_preferences));
        initializeContentView(findViewById(R.id.preferences_scroll));

        EditText usernameInput = (EditText) findViewById(R.id.input_username);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Windlass.ttf");
        ((TextView) findViewById(R.id.label_preferences_username)).setTypeface(font);
        ((Button) findViewById(R.id.button_save_preferences)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_language)).setTypeface(font);
        ((Button) findViewById(R.id.button_back)).setTypeface(font);
        usernameInput.setTypeface(font);

        Realm realm = Realm.getDefaultInstance();
        final AppSettings appSettings = realm.where(AppSettings.class).findFirst();
        if(appSettings != null) {
            usernameInput.setText(appSettings.getPlayer().getUsername());
            int imageIdentifier = getResources().getIdentifier(appSettings.getPlayer().getImage(), "mipmap", "jaanuszek0700.munchkinmanager");
            if (imageIdentifier > 0) {
                ImageButton imageButton = (ImageButton) findViewById(R.id.preferences_user_image);
                imageButton.setImageResource(imageIdentifier);
                imageButton.setTag(appSettings.getPlayer().getImage());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {

                case REQUEST_USER_IMAGE:
                    String userImageTag = intent.getStringExtra(USER_IMAGE);
                    int imageIdentifier = getResources().getIdentifier(userImageTag, "mipmap", "jaanuszek0700.munchkinmanager");
                    if (imageIdentifier > 0) {
                        ImageButton imageButton = (ImageButton) findViewById(R.id.preferences_user_image);
                        imageButton.setImageResource(imageIdentifier);
                        imageButton.setTag(userImageTag);
                    }
                    break;

                default:
                    break;
            }
    }

    /**
     * Otwiera okno wyboru ikony
     *
     * @param view widok
     */
    public void selectIcon(View view) {
        Intent intent = new Intent(this, SelectIconActivity.class);
        startActivityForResult(intent, REQUEST_USER_IMAGE);
    }

    /**
     * Zapisuje ustawienia
     *
     * @param view widok
     */
    public void saveData(View view) {
        EditText usernameInput = (EditText) findViewById(R.id.input_username);
        String playerUsername = usernameInput.getText().toString();

        TypedArray languageValues = getResources().obtainTypedArray(R.array.languages_values);
        int lang = ((Spinner) findViewById(R.id.language_selector)).getSelectedItemPosition();

        if(playerUsername.trim().equals(""))
            usernameInput.setError(view.getContext().getString(R.string.error_message_empty_field));
        else if(playerUsername.trim().length() > 10)
            usernameInput.setError(view.getContext().getString(R.string.error_message_max_characters, 10));
        else {
            Realm realm = Realm.getDefaultInstance();
            AppSettings appSettings = realm.where(AppSettings.class).findFirst();
            realm.beginTransaction();
            appSettings.setLanguage(languageValues.getString(lang));
            appSettings.getPlayer().setUsername(playerUsername);
            appSettings.getPlayer().setImage(((ImageButton) findViewById(R.id.preferences_user_image)).getTag().toString());
            realm.commitTransaction();

            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    /**
     * Wraca do poprzedniego widoku
     *
     * @param view widok
     */
    public void back(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
