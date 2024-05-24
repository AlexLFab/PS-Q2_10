package com.example.simonsays.databaseItems;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public String uid;

    public int record;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.record = 0;
    }

}