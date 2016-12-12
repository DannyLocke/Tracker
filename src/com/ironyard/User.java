package com.ironyard;

import java.util.ArrayList;

/**
 * Created by dlocke on 12/11/16.
 */
public class User {
    String name;
    String password;

    ArrayList<Twitter> twitterEntries = new ArrayList<>();

    public User(String name, String password) {

        this.name = name;
        this.password = password;
    }
}
