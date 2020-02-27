package com.aasoo.freshifybeta.model;

public class MyNotification {

    private String notification_id;
    private String notification_type;
    private String notification_msg;
    private String notification_date;
    private String notification_reference;
    private String notification_title;

    public MyNotification( ) {
        this.notification_id = notification_id;
        this.notification_type = notification_type;
        this.notification_msg = notification_msg;
        this.notification_date = notification_date;
        this.notification_reference = notification_reference;
        this.notification_title = notification_title;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_msg() {
        return notification_msg;
    }

    public void setNotification_msg(String notification_msg) {
        this.notification_msg = notification_msg;
    }

    public String getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(String notification_date) {
        this.notification_date = notification_date;
    }

    public String getNotification_reference() {
        return notification_reference;
    }

    public void setNotification_reference(String notification_reference) {
        this.notification_reference = notification_reference;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }
}
