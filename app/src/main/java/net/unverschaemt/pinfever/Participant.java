package net.unverschaemt.pinfever;

import android.provider.Telephony;

/**
 * Created by kkoile on 12.05.15.
 */
public class Participant {

    private String id;
    private String player;
    private int state;
    private int score;

    public Participant(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
