package com.finappl.models;

/**
 * Created by ajit on 8/12/16.
 */

public class TagsMO {
    private String TAG_ID;
    private String TAG_TYPE;
    private String TAG_TYPE_ID;
    private String TAGS;
    private String USER_ID;

    public String getTAG_TYPE() {
        return TAG_TYPE;
    }

    public void setTAG_TYPE(String TAG_TYPE) {
        this.TAG_TYPE = TAG_TYPE;
    }

    public String getTAG_TYPE_ID() {
        return TAG_TYPE_ID;
    }

    public void setTAG_TYPE_ID(String TAG_TYPE_ID) {
        this.TAG_TYPE_ID = TAG_TYPE_ID;
    }

    public String getTAGS() {
        return TAGS;
    }

    public void setTAGS(String TAGS) {
        this.TAGS = TAGS;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getTAG_ID() {
        return TAG_ID;
    }

    public void setTAG_ID(String TAG_ID) {
        this.TAG_ID = TAG_ID;
    }
}
