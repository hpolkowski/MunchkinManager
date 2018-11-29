package jaanuszek0700.munchkinmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class NewRoomActivity extends FullscreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        initializeContentView(findViewById(R.id.fullscreen_new_room));
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Windlass.ttf");
        ((Button) findViewById(R.id.button_start_game)).setTypeface(font);
        ((Button) findViewById(R.id.button_back)).setTypeface(font);
        ((EditText) findViewById(R.id.input_game_name)).setTypeface(font);
        ((TextView) findViewById(R.id.label_game_name)).setTypeface(font);
    }

    /**
     * Uruchamia pokój gry
     *
     * @param view widok
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        EditText gameNameInput = (EditText) findViewById(R.id.input_game_name);
        String gameName = gameNameInput.getText().toString();

        if(gameName.trim().equals(""))
            gameNameInput.setError(view.getContext().getString(R.string.error_message_empty_field));
        else if(gameName.trim().length() > 10)
            gameNameInput.setError(view.getContext().getString(R.string.error_message_max_characters, 10));
        else {
            intent.putExtra(GameActivity.ROOM_NAME, gameName);
            intent.putExtra(GameActivity.IS_HOST, true);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Wraca do poprzedniego widoku
     *
     * @param view widok
     */
    public void back(View view) {
        finish();
    }
}
