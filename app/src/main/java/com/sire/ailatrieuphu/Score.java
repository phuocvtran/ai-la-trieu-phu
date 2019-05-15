package com.sire.ailatrieuphu;

import android.support.annotation.NonNull;

public class Score implements Comparable<Score> {
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

    public String toString() {
        return this.user + " - " + this.score;
    }

    // Sắp xếp
    @Override
    public int compareTo(Score o) {
        int comparedUser = Integer.parseInt(o.getScore());
        // Giảm dần
        return comparedUser - Integer.parseInt(this.getScore());
        // Tăng dần
        //return Integer.parseInt(this.getScore()) - comparedUser;
    }
}
