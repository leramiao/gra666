package logic;

//package com.example.projekt.logic;

//Klasa user po ktorej dziedzicza admin oraz client

public class User {
    public int id;
    public int maxID =0;
    public String login;
    public String password;
    public String PFPfilename;


    User(int id, String login, String password) {
        this.id = id;
        if(maxID < id) maxID = id;
        this.login = login;
        this.password = password;
        this.PFPfilename = "default.jpg";
    }
    public User(String login, String password) {
        this.id = ++maxID;
        this.login = login;
        this.password = password;
        this.PFPfilename = "defaultPFP.png";
    }

    public int getId (){
        return id;

    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {

        return id + ":" +  login + ":" + password;
    }

    public String getPFPfilename() {
        return PFPfilename;
    }
}