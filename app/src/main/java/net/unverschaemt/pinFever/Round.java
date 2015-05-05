package net.unverschaemt.pinFever;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Round implements Serializable{
    private Collection<Question> questions;

    public Round(Collection<Question> questions){
        this.questions = questions;
    }

    public Collection<Question> getQuestions(){
        return questions;
    }

    public int getOwnScore(){
        int ownScore = 0;
        for(Question question : questions){
            ownScore += question.getOwnScore();
        }
        return ownScore;
    }
    public int getOpponentScore(){
        int opponentScore =0;
        for(Question question : questions){
            opponentScore += question.getOpponentScore();
        }
        return opponentScore;
    }

}
