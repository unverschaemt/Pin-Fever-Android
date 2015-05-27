package net.unverschaemt.pinfever;

import java.io.Serializable;

/**
 * Created by kkoile on 12.05.15.
 */
public class Turninformation implements Serializable {

    private long id;
    private float answerLat;
    private float answerLong;
    private float distance;

    public Turninformation() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}
