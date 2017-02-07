package com.finappl.utils;

import com.finappl.models.UserMO;

import java.util.List;

/**
 * Created by ajit on 6/2/17.
 */

public class TestData {
    private static UserMO user;

    static {
        user = new UserMO();
        user.setUSER_ID("8");
        user.setNAME("AJIT");
        user.setEMAIL("ajitkamathk@gmail.com");
        user.setCUR_CODE("₹");
        user.setMETRIC("INDIAN");
    }

    public static UserMO getUser() {
        return user;
    }
}
