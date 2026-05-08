package com.healthos.frontend.controller;

import com.healthos.backend.database.MongoDBHelper;

public class LoginController {

    public enum LoginResult { SUCCESS, INVALID_CREDENTIALS }

    public enum RegisterResult { SUCCESS, USER_EXISTS, EMPTY_FIELDS, PASSWORD_TOO_SHORT }

    private final MongoDBHelper db;

    public LoginController(MongoDBHelper db) {
        this.db = db;
    }

    public LoginResult login(String userID, String password) {
        if (db.verifyPassword(userID, password)) {
            return LoginResult.SUCCESS;
        }
        return LoginResult.INVALID_CREDENTIALS;
    }

    public RegisterResult register(String userID, String password) {
        if (userID.isEmpty() || password.isEmpty()) {
            return RegisterResult.EMPTY_FIELDS;
        }
        if (password.length() < 6) {
            return RegisterResult.PASSWORD_TOO_SHORT;
        }
        return db.saveUser(userID, password) ? RegisterResult.SUCCESS : RegisterResult.USER_EXISTS;
    }
}
