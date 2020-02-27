package com.aasoo.freshifybeta.model;

public class Contact {
    private String contact_name;
    private String contact_phone;
    private String contact_email;
    private String contact_other;
    private String contact_id;

    public Contact() {
        this.contact_name = contact_name;
        this.contact_phone = contact_phone;
        this.contact_email = contact_email;
        this.contact_other = contact_other;
        this.contact_id = contact_id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_other() {
        return contact_other;
    }

    public void setContact_other(String contact_other) {
        this.contact_other = contact_other;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }
}
