package net.unverschaemt.pinfever;

import java.io.Serializable;

/**
 * Created by D060338 on 05.05.2015.
 */
public class User implements Serializable{
    String userName;
    String id;
    String name;
    String email;
    int avatar;

    public User(String id, String userName, String name, String email, int avatar){
        this.id = id;
        this.userName = userName;
        this.avatar = avatar;
        this.name  = name;
        this.email = email;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString(){
        return userName;
    }


}
