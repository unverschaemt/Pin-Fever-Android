package net.unverschaemt.pinfever;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Game implements Serializable {
    private String id;
    private List<Round> rounds;
    private GameState state;
    private Round activeRound;
    private List<Participant> participants;
    private String activeRoundID;

    public Game() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Round getActiveRound() {
        return activeRound;
    }

    public void setActiveRound(Round activeRound) {
        this.activeRound = activeRound;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setActiveRoundID(String id) {
        this.activeRoundID = id;
    }

    public String getActiveRoundID() {
        return this.activeRoundID;
    }
}
