package com.healthos;

import com.healthos.backend.database.*;
import com.healthos.backend.model.*;
import com.healthos.frontend.view.*;
import com.healthos.backend.model.*;

public class main {
    public static void main(String[] args) {
        MongoDBHelper db = new MongoDBHelper();
        Login loginPage = new Login(db.getloginInfo(), db);
    }
}