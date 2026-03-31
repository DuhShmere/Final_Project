package com.loginapp;

public class main {
    public static void main(String[] args) {
        MongoDBHelper db = new MongoDBHelper();
        Login loginPage = new Login(db.getloginInfo(), db);
    }
}