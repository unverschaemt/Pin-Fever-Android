package net.unverschaemt.pinfever;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Question implements Serializable{
    private int ownScore = 0;
    private int opponentScore = 0;

    private long id;
    private String text;
    private float answerLat;
    private float answerLong;
    private int state;
    private long participantWhoWon;
    private HashMap<Long, Turninformation> turnInformation;

    public Question(){

    }

    public int getOwnScore(){
        return ownScore;
    }
    public int getOpponentScore(){
        return opponentScore;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getParticipantWhoWon() {
        return participantWhoWon;
    }

    public void setParticipantWhoWon(long participantWhoWon) {
        this.participantWhoWon = participantWhoWon;
    }

    public void setTurninformation(HashMap<Long, Turninformation> turnInformation){
        this.turnInformation = turnInformation;
    }

    public void addTurninformation(Long user, Turninformation turninformation){
        this.turnInformation.put(user, turninformation);
    }

    public java.util.HashMap<Long, Turninformation> getTurninformation() {
        return turnInformation;
    }
}
