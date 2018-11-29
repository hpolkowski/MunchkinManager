package jaanuszek0700.munchkinmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import jaanuszek0700.munchkinmanager.adapters.GameAdapter;
import jaanuszek0700.munchkinmanager.models.AppSettings;
import jaanuszek0700.munchkinmanager.models.Game;

public class SelectRoomActivity extends FullscreenActivity {

    private GameAdapter gameAdapter;

    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {

        Game game;

        @Override
        public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
            game = new Game(discoveredEndpointInfo.getEndpointName(), endpointId);
            gameAdapter.add(game);
        }

        @Override
        public void onEndpointLost(String s) {
            gameAdapter.remove(game);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        initializeContentView(findViewById(R.id.fullscreen_select_room));

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Windlass.ttf");
        ((Button) findViewById(R.id.button_join_game)).setTypeface(font);
        ((Button) findViewById(R.id.button_back)).setTypeface(font);
        ((TextView) findViewById(R.id.select_game_room_textView)).setTypeface(font);

        Realm realm = Realm.getDefaultInstance();
        final AppSettings appSettings = realm.where(AppSettings.class).findFirst();

        ListView gameListView = (ListView) findViewById(R.id.games_listView);
        gameAdapter = new GameAdapter(this);
        gameListView.setAdapter(gameAdapter);
        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Game game = gameAdapter.getItem(position);

                Intent intent = new Intent (getApplicationContext(), GameActivity.class);
                intent.putExtra(GameActivity.PLAYER_NAME, appSettings.getPlayer().getUsername());
                intent.putExtra(GameActivity.CONNECTION_ID, game.getEndpointId());
                intent.putExtra(GameActivity.ROOM_NAME, game.getName());
                startActivity(intent);
            }
        });

        Nearby.getConnectionsClient(this).startDiscovery(BuildConfig.APPLICATION_ID, mEndpointDiscoveryCallback, new DiscoveryOptions(Strategy.P2P_CLUSTER));

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
