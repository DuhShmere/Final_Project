package com.healthos;

import com.healthos.backend.database.MongoDBHelper;
import com.healthos.frontend.view.Login;

public class main {
    public static void main(String[] args) {
        MongoDBHelper db = new MongoDBHelper();
        Login loginPage = new Login(db.getloginInfo(), db);
    }
}