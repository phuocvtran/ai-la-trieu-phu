package com.sire.ailatrieuphu;

import android.support.annotation.NonNull;

public class User {
    private String name;
    private int score;

    public User() {
        this.name = "default";
        this.score = 0;
    }

    public User(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScored() {
        return score;
    }

    public void setScored(int scored) {
        this.score = scored;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compare(User user) {
        if (this.score > user.score)
            return 1;
        else if (this.score < user.score)
            return -1;
        return 0;
    }

    @NonNull
    public String toString() {
        return this.name + " - " + this.score;
    }
}
