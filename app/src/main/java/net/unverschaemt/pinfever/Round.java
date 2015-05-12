package net.unverschaemt.pinfever;

import java.io.Serializable;
import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class Round implements Serializable {
    private long id;
    private List<Question> questions;
    private String category;

    public Round(List<Question> questions) {
        this.questions = questions;
    }

    public Round() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOwnScore() {
        int ownScore = 0;
        for (Question question : questions) {
            ownScore += question.getOwnScore();
        }
        return ownScore;
    }

    public int getOpponentScore() {
        int opponentScore = 0;
        for (Question question : questions) {
            opponentScore += question.getOpponentScore();
        }
        return opponentScore;
    }

    public void setQuestions(List<Question> questions){
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

}
