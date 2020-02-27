package com.aasoo.freshifybeta.model;

public class Referral {

    public String referral_id;
    public String referral_date;
    public String referral_type;
    public String referral_user_id;

    public Referral() {
        this.referral_id = referral_id;
        this.referral_date = referral_date;
        this.referral_type = referral_type;
        this.referral_user_id = referral_user_id;
    }

    public String getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(String referral_id) {
        this.referral_id = referral_id;
    }

    public String getReferral_date() {
        return referral_date;
    }

    public void setReferral_date(String referral_date) {
        this.referral_date = referral_date;
    }

    public String getReferral_type() {
        return referral_type;
    }

    public void setReferral_type(String referral_type) {
        this.referral_type = referral_type;
    }

    public String getReferral_user_id() {
        return referral_user_id;
    }

    public void setReferral_user_id(String referral_user_id) {
        this.referral_user_id = referral_user_id;
    }
}
