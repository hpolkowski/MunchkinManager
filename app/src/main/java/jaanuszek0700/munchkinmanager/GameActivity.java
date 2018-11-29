package jaanuszek0700.munchkinmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import jaanuszek0700.munchkinmanager.adapters.PlayerAdapter;
import jaanuszek0700.munchkinmanager.models.AppSettings;
import jaanuszek0700.munchkinmanager.models.Player;
import jaanuszek0700.munchkinmanager.models.PlayerData;

public class GameActivity extends FullscreenActivity implements PlayerAdapter.ItemClickListener {

    private PlayerAdapter playerAdapter;
    private Player player;
    private String roomName;
    private List<Player> players = new ArrayList<>();

    public static final String ROOM_NAME = "room_name";
    public static final String IS_HOST = "is_host";
    public static final String PLAYER_NAME = "player_name";
    public static final String CONNECTION_ID = "connection_id";

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {

        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(getBaseContext()).acceptConnection(endpointId, mPayloadCallback);
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    try {
                        Payload payload = Payload.fromBytes(player.toPlayerData().toBytes());
                        Nearby.getConnectionsClient(getBaseContext()).sendPayload(endpointId, payload);
                    } catch (IOException e) {
                        Log.e("EXCEPTION", e.getMessage(), e);
                    }
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    break;
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
        }
    };

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // A new payload is being sent over.
            try {
                Player player = PlayerData.fromBytes(payload.asBytes()).toPlayer();
                players.add(player);
                playerAdapter.notifyDataSetChanged();
                Toast.makeText(GameActivity.this, "Payload: " + player.getUsername(), Toast.LENGTH_SHORT).show();
            } catch (IOException | ClassNotFoundException e) {
                Log.e("EXCEPTION", e.getMessage(), e);
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Payload progress has updated.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Realm realm = Realm.getDefaultInstance();
        final AppSettings appSettings = realm.where(AppSettings.class).findFirst();
        if(appSettings != null) {
            player = appSettings.getPlayer();
            setLanguage(appSettings.getLanguage());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeContentView(findViewById(R.id.fullscreen_game));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        TextView roomNameTextView = (TextView) findViewById(R.id.room_name);
        final TextView playerInventory = (TextView) findViewById(R.id.actual_inventory);
        final TextView playerModifier = (TextView) findViewById(R.id.actual_modifier);

        roomName = getIntent().getStringExtra(ROOM_NAME);
        roomNameTextView.setText(roomName);
        playerInventory.setText(String.valueOf(player.getInventory()));
        playerModifier.setText(String.valueOf(player.getModifiers()));

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Windlass.ttf");
        ((TextView) findViewById(R.id.textView_level)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_modifier)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_inventory)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_room_name)).setTypeface(font);
        playerModifier.setTypeface(font);
        playerInventory.setTypeface(font);
        roomNameTextView.setTypeface(font);

        RecyclerView playersRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_players);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        playerAdapter = new PlayerAdapter(this, players);
        playerAdapter.setClickListener(this);
        playersRecyclerView.setAdapter(playerAdapter);

        playerModifier.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                realm.beginTransaction();
                player.setModifiers(0);
                realm.commitTransaction();
                vibrator.vibrate(100);
                playerModifier.setText(String.valueOf(player.getModifiers()));
                return true;
            }
        });

        playerInventory.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                realm.beginTransaction();
                player.setInventory(0);
                realm.commitTransaction();
                vibrator.vibrate(100);
                playerInventory.setText(String.valueOf(player.getInventory()));
                return true;
            }
        });

        if ( getIntent().getBooleanExtra(IS_HOST, false) )
            startAdvertising();
        else
            connectToHost(getIntent().getStringExtra(PLAYER_NAME), getIntent().getStringExtra(CONNECTION_ID));

    }

    private void connectToHost(String username, String endpointId) {
        Nearby.getConnectionsClient(getBaseContext()).requestConnection(username, endpointId, mConnectionLifecycleCallback).addOnSuccessListener(
            new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unusedResult) {
                    Toast.makeText(GameActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                }
            })
            .addOnFailureListener(
            new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GameActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    // Nearby Connections failed to request the connection.
                }
            });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + playerAdapter.getItem(position).getUsername(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
    }

    private void startAdvertising() {
        Nearby.getConnectionsClient(getBaseContext()).startAdvertising(roomName, BuildConfig.APPLICATION_ID, mConnectionLifecycleCallback, new AdvertisingOptions(Strategy.P2P_CLUSTER))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're advertising!
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start advertising.
                            }
                        });
    }

    /**
     * Otwiera okno rzucania kośćmi
     *
     * @param view widok
     */
    public void rollDices(View view) {
        Intent intent = new Intent(this, DiceActivity.class);
        startActivity(intent);
    }

    /**
     * Kończy grę
     *
     * @param view widok
     */
    public void quitGame(View view) {
        final Intent intent = new Intent(this, MenuActivity.class);
        new AlertDialog.Builder(this)
                .setMessage(R.string.text_want_to_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        player.setLevel(1);
                        player.setInventory(0);
                        player.setModifiers(0);
                        realm.commitTransaction();

                        Nearby.getConnectionsClient(getBaseContext()).stopAdvertising();

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.text_no, null)
                .show();
    }

    /**
     * Ustawia jezyk aplikacji
     *
     * @param lang język
     */
    private void setLanguage(String lang) {
        Locale myLocale = new Locale(lang);

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * Zwiększa modyfikator
     *
     * @param view widok
     */
    public void increaseModifier(View view) {
        TextView playerModifier = (TextView) findViewById(R.id.actual_modifier);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        player.setModifiers(player.getModifiers() + 1);
        realm.commitTransaction();
        playerModifier.setText(String.valueOf(player.getModifiers()));

    }

    /**
     * Zmniejsza modyfikator
     *
     * @param view widok
     */
    public void decreaseModifier(View view) {
        TextView playerModifier = (TextView) findViewById(R.id.actual_modifier);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        player.setModifiers(player.getModifiers() - 1);
        realm.commitTransaction();
        playerModifier.setText(String.valueOf(player.getModifiers()));

    }

    /**
     * Zwiększa ekwipunek
     *
     * @param view widok
     */
    public void increaseInventory(View view) {
        TextView playerInventory = (TextView) findViewById(R.id.actual_inventory);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        player.setInventory(player.getInventory() + 1);
        realm.commitTransaction();
        playerInventory.setText(String.valueOf(player.getInventory()));

    }

    /**
     * Zmniejsza ekwipunek
     *
     * @param view widok
     */
    public void decreaseInventory(View view) {
        TextView playerInventory = (TextView) findViewById(R.id.actual_inventory);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        player.setInventory(player.getInventory() - 1);
        realm.commitTransaction();
        playerInventory.setText(String.valueOf(player.getInventory()));

    }
}
