package net.unverschaemt.pinfever;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by D060338 on 05.05.2015.
 */
public class User implements Serializable {

    private String userName;
    private String id;
    private String email;
    private int score;
    private SerialBitmap avatar;

    public User() {
        avatar = new SerialBitmap();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Bitmap getAvatar() {
        return avatar.bitmap;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar.bitmap = avatar;
    }

    @Override
    public String toString() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            return this.id.equals(((User) o).getId());
        }
        return super.equals(o);
    }
}
