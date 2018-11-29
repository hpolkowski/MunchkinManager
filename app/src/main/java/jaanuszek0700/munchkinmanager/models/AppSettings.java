package jaanuszek0700.munchkinmanager.models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class AppSettings extends RealmObject {
    private Player player;
    private String language;

    public AppSettings() {
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
