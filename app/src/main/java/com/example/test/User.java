package com.example.test;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
    public class User {

        public String Username;
        public String Phone;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.Username = username;
            this.Phone = email;
        }

    }

