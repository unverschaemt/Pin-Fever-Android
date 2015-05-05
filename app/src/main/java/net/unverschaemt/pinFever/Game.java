package net.unverschaemt.pinFever;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Game implements Serializable{
    final private String id;
    private User opponent;
    private List<Round> rounds;
    private boolean finished = false;

    public Game(String id, User opponent){
        this.id = id;
        this.opponent = opponent;
    }

    public String getOpponentName() {
        return opponent.getUserName();
    }
    public int getOpponentAvatar() {
        return opponent.getAvatar();
    }
    public String getID() {
        return id;
    }

    public void addRounds(List<Round> rounds){
        this.rounds = rounds;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public int getOwnScore() {
        int ownScore = 0;
        for (Round round : rounds){
            ownScore += round.getOwnScore();
        }
        return ownScore;
    }

    public int getOpponentScore() {
        int opponentScore = 0;
        for (Round round : rounds) {
            opponentScore += round.getOpponentScore();
        }
        return opponentScore;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
