package com.sire.ailatrieuphu;

import android.support.annotation.NonNull;

public class Score {
    private String user, score;

    public Score() {}

    public Score(String name, String score) {
        this.user = name;
        this.score = score;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String name) {
        this.user = name;
    }

    @NonNull
    public String toString() {
        return this.user + " - " + this.score;
    }
}
