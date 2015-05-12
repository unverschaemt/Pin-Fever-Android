package net.unverschaemt.pinfever;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Game implements Serializable {
    private long id;
    private List<Round> rounds;
    private int state;
    private long activeRound = -1;
    private List<Participant> participants;

    public Game() {

    }

    public long getId() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public int getOwnScore() {
        int ownScore = 0;
        for (Round round : rounds) {
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

    public long getActiveRound() {
        return activeRound;
    }

    public void setActiveRound(long activeRound) {
        this.activeRound = activeRound;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
