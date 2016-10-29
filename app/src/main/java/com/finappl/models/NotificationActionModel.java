package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ajit on 12/8/15.
 */
public class NotificationActionModel implements Serializable {

    private String notificationActionStr;
    private String notificationIdStr;
    private String notificationTypeStr;

    //this holds whatever the notification object is
    private Object notificationObject;

    public String getNotificationActionStr() {
        return notificationActionStr;
    }

    public void setNotificationActionStr(String notificationActionStr) {
        this.notificationActionStr = notificationActionStr;
    }

    public String getNotificationIdStr() {
        return notificationIdStr;
    }

    public void setNotificationIdStr(String notificationIdStr) {
        this.notificationIdStr = notificationIdStr;
    }

    public String getNotificationTypeStr() {
        return notificationTypeStr;
    }

    public void setNotificationTypeStr(String notificationTypeStr) {
        this.notificationTypeStr = notificationTypeStr;
    }

    public Object getNotificationObject() {
        return notificationObject;
    }

    public void setNotificationObject(Object notificationObject) {
        this.notificationObject = notificationObject;
    }
}
