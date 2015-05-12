package net.unverschaemt.pinfever;

import android.provider.Telephony;

/**
 * Created by kkoile on 12.05.15.
 */
public class Participant {

    private long id;
    private long player;
    private int state;
    private int score;

    public Participant(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
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
