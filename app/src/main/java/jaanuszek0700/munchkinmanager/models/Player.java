package jaanuszek0700.munchkinmanager.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.realm.RealmObject;

public class Player extends RealmObject {

    private String username;
    private String image;
    private int level = 1;
    private int inventory = 0;
    private int modifiers = 0;

    public Player() {
    }

    public Player(String username, int level, int inventory, int modifiers, String image) {
        this.username = username;
        this.image = image;
        this.level = level;
        this.inventory = inventory;
        this.modifiers = modifiers;
    }

    public int getPower() {
        return level + inventory + modifiers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public PlayerData toPlayerData() {
        return new PlayerData(this.username, this.level, this.inventory, this.modifiers, this.image);
    }
}

