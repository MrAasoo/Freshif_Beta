package com.aasoo.freshifybeta.model;

public class Dashboard {

    private String dash_sr_no;
    private String dash_date;
    private String dash_detail;
    private String dash_due_amount;
    private String dash_remark;
    private String dash_type;
    private String dash_amount;

    public Dashboard() {
        this.dash_sr_no = dash_sr_no;
        this.dash_date = dash_date;
        this.dash_detail = dash_detail;
        this.dash_due_amount = dash_due_amount;
        this.dash_remark = dash_remark;
        this.dash_type = dash_type;
        this.dash_amount = dash_amount;
    }

    public String getDash_sr_no() {
        return dash_sr_no;
    }

    public void setDash_sr_no(String dash_sr_no) {
        this.dash_sr_no = dash_sr_no;
    }

    public String getDash_date() {
        return dash_date;
    }

    public void setDash_date(String dash_date) {
        this.dash_date = dash_date;
    }

    public String getDash_detail() {
        return dash_detail;
    }

    public void setDash_detail(String dash_detail) {
        this.dash_detail = dash_detail;
    }

    public String getDash_due_amount() {
        return dash_due_amount;
    }

    public void setDash_due_amount(String dash_due_amount) {
        this.dash_due_amount = dash_due_amount;
    }

    public String getDash_remark() {
        return dash_remark;
    }

    public void setDash_remark(String dash_remark) {
        this.dash_remark = dash_remark;
    }

    public String getDash_type() {
        return dash_type;
    }

    public void setDash_type(String dash_type) {
        this.dash_type = dash_type;
    }

    public String getDash_amount() {
        return dash_amount;
    }

    public void setDash_amount(String dash_amount) {
        this.dash_amount = dash_amount;
    }
}
