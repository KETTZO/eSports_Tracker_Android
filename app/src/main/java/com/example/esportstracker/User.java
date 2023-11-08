package com.example.esportstracker;

import com.google.gson.Gson;

import java.sql.Blob;

public class User {
    String aliasUser;
    String email;
    String pass;
    String name;
    byte[] avatar;
/*
    public User(String username, String email, String password) {
        this.alias = username;
        this.email = email;
        this.pass = password;
    }*/
    public String getAlias() {
        return aliasUser;
    }

    public void setAlias(String username) {
        this.aliasUser = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String name) {
        this.pass = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
