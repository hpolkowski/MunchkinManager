package jaanuszek0700.munchkinmanager.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Game extends RealmObject {

    private String name;
    private String endpointId;
    private RealmList<Player> players;

    public Game() {
    }

    public Game(String name) {
        this.name = name;
    }

    public Game(String name, String endpointId) {
        this.name = name;
        this.endpointId = endpointId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public RealmList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(RealmList<Player> players) {
        this.players = players;
    }
}
