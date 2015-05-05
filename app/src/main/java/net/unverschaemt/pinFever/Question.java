package net.unverschaemt.pinFever;

import java.io.Serializable;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Question implements Serializable{
    private String id;
    private String category;
    private String question;
    private int ownScore = 0;
    private int opponentScore = 0;
    private boolean played = false;

    public Question(String id, String category, String question){
        this.id = id;
        this.category = category;
        this.question = question;
    }

    public Question(String id, String category, String question, int ownScore, int opponentScore){
        this(id, category, question);
        this.ownScore = ownScore;
        this.opponentScore = opponentScore;
        this.played = true;
    }

    public boolean isPlayed(){
        return played;
    }
    public void setPlayed(boolean played){
        this.played = played;
    }
    public int getOwnScore(){
        return ownScore;
    }
    public int getOpponentScore(){
        return opponentScore;
    }

}
