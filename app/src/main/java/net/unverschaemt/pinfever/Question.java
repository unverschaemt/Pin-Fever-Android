package net.unverschaemt.pinfever;

import java.io.Serializable;
import java.util.*;
import java.util.Map;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Question implements Serializable {
    private int ownScore = 0;
    private int opponentScore = 0;

    private String id;
    private String text;
    private float answerLat;
    private float answerLong;
    private String answerText;
    private int state;
    private String participantWhoWon;
    private java.util.Map<String, Turninformation> turnInformation;

    public Question() {

    }
    
    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getOwnScore() {
        return ownScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getAnswerLat() {
        return answerLat;
    }

    public void setAnswerLat(float answerLat) {
        this.answerLat = answerLat;
    }

    public float getAnswerLong() {
        return answerLong;
    }

    public void setAnswerLong(float answerLong) {
        this.answerLong = answerLong;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getParticipantWhoWon() {
        return participantWhoWon;
    }

    public void setParticipantWhoWon(String participantWhoWon) {
        this.participantWhoWon = participantWhoWon;
    }

    public void setTurninformation(Map<String, Turninformation> turnInformation) {
        this.turnInformation = turnInformation;
    }

    public void addTurninformation(String user, Turninformation turninformation) {
        this.turnInformation.put(user, turninformation);
    }

    public Map<String, Turninformation> getTurninformation() {
        return turnInformation;
    }
}
