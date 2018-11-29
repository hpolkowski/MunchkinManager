package jaanuszek0700.munchkinmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectIconActivity extends FullscreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon);
        initializeContentView(findViewById(R.id.fullscreen_select_icon));
        initializeContentView(findViewById(R.id.fullscreen_select_icon_scrollview));

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Windlass.ttf");
        ((Button) findViewById(R.id.button_back)).setTypeface(font);
    }

    /**
     * Wybiera ikonę
     *
     * @param view widok
     */
    public void selectIcon(View view) {
        Intent intent = new Intent();
        intent.putExtra(PreferencesActivity.USER_IMAGE, view.getTag().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
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
